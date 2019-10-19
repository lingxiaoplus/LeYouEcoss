package com.lingxiao.service;

import com.github.wxpay.sdk.WXPayConstants;
import com.lingxiao.client.AddressClient;
import com.lingxiao.client.GoodsClient;
import com.lingxiao.common.IdWorker;
import com.lingxiao.dto.AddressDto;
import com.lingxiao.dto.CartDto;
import com.lingxiao.dto.OrderDto;
import com.lingxiao.enums.ExceptionEnum;
import com.lingxiao.enums.OrderStatusEnum;
import com.lingxiao.enums.PayStatusEnum;
import com.lingxiao.exception.LyException;
import com.lingxiao.interceptor.LoginInterceptor;
import com.lingxiao.mapper.OrderDetailMapper;
import com.lingxiao.mapper.OrderMapper;
import com.lingxiao.mapper.OrderStatusMapper;
import com.lingxiao.pojo.*;
import com.lingxiao.utils.PayHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service("orderService")
@Slf4j
public class OrderService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper detailMapper;
    @Autowired
    private OrderStatusMapper statusMapper;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private PayHelper payHelper;

    @Transactional
    public Long createOrder(OrderDto dto) {
        //1.创建订单
        Order order = new Order();
        //1.1 订单编号
        long id = idWorker.nextId();
        order.setOrderId(id);
        order.setCreateTime(new Date());
        order.setPaymentType(dto.getPaymentType());
        //1.2 用户信息
        /*UserInfo userInfo = LoginInterceptor.getUserInfo();
        order.setUserId(userInfo.getId().toString());*/
        UserInfo userInfo = new UserInfo(28L,"lingxiao");
        order.setUserId(userInfo.getId().toString());
        order.setBuyerNick(userInfo.getUsername());
        order.setBuyerMessage("");
        order.setBuyerRate(0);
        //1.3 收货地址
        Long addressId = dto.getAddressId();
        AddressDto addressDto = AddressClient.findById(addressId);
        if (addressDto == null){
            throw new LyException(ExceptionEnum.ILLEGA_ARGUMENT);
        }
        order.setReceiver(addressDto.getName());
        order.setReceiverAddress(addressDto.getAddress());
        order.setReceiverState(addressDto.getState());
        order.setReceiverCity(addressDto.getCity());
        order.setReceiverDistrict(addressDto.getDistrict());
        order.setReceiverMobile(addressDto.getPhone());
        order.setReceiverZip(addressDto.getZipCode());
        //1.4 金额
        Map<Long, Integer> skuMap = dto
                .getCarts()
                .stream()
                .collect(Collectors.toMap(CartDto::getSkuId, CartDto::getNum));
        Set<Long> set = skuMap.keySet();
        List<Long> skuIds = new ArrayList<>(set);
        List<Sku> skuList = goodsClient.getSkuListByIds(skuIds);
        long totalPrice = 0;
        List<OrderDetail> detailList = new ArrayList<>();
        for (Sku sku : skuList) {
            totalPrice += sku.getPrice() * skuMap.get(sku.getId());

            OrderDetail detail = new OrderDetail();
            detail.setSkuId(sku.getId());
            detail.setTitle(sku.getTitle());
            detail.setOwnSpec(sku.getOwnSpec());
            detail.setNum(skuMap.get(sku.getId()));
            detail.setOrderId(order.getOrderId());
            detail.setPrice(sku.getPrice());
            detail.setImage(StringUtils.substringBefore(sku.getImages(),","));
            detailList.add(detail);
        }
        order.setTotalPay(totalPrice);
        //总金额 + 邮费 - 优惠金额
        order.setPostFee(0L);
        Long postFee = order.getPostFee();
        order.setActualPay(totalPrice + postFee);

        //1.5写入数据库
        int count = orderMapper.insertSelective(order);
        if (count != 1){
            log.error("创建订单失败，订单id：{}", order.getOrderId());
            throw new LyException(ExceptionEnum.CREATE_ORDER_ERROE);
        }
        //2.创建订单详情
        count = detailMapper.insertList(detailList);
        if (count != detailList.size()){
            log.error("创建订单失败，订单id：{}", order.getOrderId());
            throw new LyException(ExceptionEnum.CREATE_ORDER_ERROE);
        }
        //3.创建订单状态
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setStatus(OrderStatusEnum.UN_PAY.code());
        orderStatus.setCreateTime(new Date());
        orderStatus.setOrderId(order.getOrderId());

        count = statusMapper.insertSelective(orderStatus);
        if (count != 1){
            log.error("创建订单失败，订单id：{}", order.getOrderId());
            throw new LyException(ExceptionEnum.CREATE_ORDER_ERROE);
        }
        //4.减库存  分布式事务  远程调用接口，抛出异常之后 这边也会执行回滚
        goodsClient.decreaseStock(dto.getCarts());
        return order.getOrderId();
    }

    public Order queryOrderById(Long id) {
        Order order = orderMapper.selectByPrimaryKey(id);
        if (order == null){
            throw new LyException(ExceptionEnum.ORDER_NOT_FOUND);
        }
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderId(id);
        List<OrderDetail> details = detailMapper.select(orderDetail);
        if (CollectionUtils.isEmpty(details)){
            throw new LyException(ExceptionEnum.ORDER_DETAIL_NOT_FOUND);
        }
        order.setOrderDetails(details);

        OrderStatus orderStatus = statusMapper.selectByPrimaryKey(id);
        if (orderStatus == null){
            throw new LyException(ExceptionEnum.ORDER_STATUS_NOT_FOUND);
        }
        order.setOrderStatus(orderStatus);
        return order;
    }

    /**
     * 创建微信支付二维码链接
     * @param id
     * @return
     */
    public String createPayUrl(Long id) {
        Order order = queryOrderById(id);
        Integer status = order.getOrderStatus().getStatus();
        if (OrderStatusEnum.UN_PAY.code() != status){
            throw new LyException(ExceptionEnum.ORDER_STATUS_ERROR);
        }
        Long actualPay = order.getActualPay();
        //商品描述
        String desc = order.getOrderDetails().get(0).getTitle();
        String url = payHelper.createOrder(id, actualPay, desc);
        return url;
    }


    /**
     * 接收微信订单结果
     * @param result
     */
    public void handlerNotify(Map<String, String> result) {
        //下单是否成功
        payHelper.isSuccess(result);
        //校验签名
        payHelper.isValidSign(result);

        //判断金额是否一致
        String total_fee = result.get("total_fee");
        if (StringUtils.isBlank(total_fee)){
            throw new LyException(ExceptionEnum.INVALID_ORDER_PARAM);
        }
        String outTradeNo = result.get("out_trade_no");
        Long orderId = Long.valueOf(outTradeNo);
        Order order = orderMapper.selectByPrimaryKey(orderId);
        Long actualPay = order.getActualPay();
        if (!total_fee.equals(String.valueOf(actualPay))){
            throw new LyException(ExceptionEnum.INVALID_ORDER_PARAM);
        }
        //修改订单状态
        OrderStatus status = new OrderStatus();
        status.setStatus(OrderStatusEnum.PAYED.code());
        status.setOrderId(orderId);
        status.setPaymentTime(new Date());
        int count = statusMapper.updateByPrimaryKeySelective(status);
        if (count != 1){
            throw new LyException(ExceptionEnum.UPDATE_ORDER_STATUS_ERROR);
        }
        log.info("微信支付结果，{}",orderId);
    }

    public PayStatusEnum queryOrderStatus(Long id) {
        OrderStatus status = statusMapper.selectByPrimaryKey(id);
        if (OrderStatusEnum.UN_PAY.code() != status.getStatus()){
            //如果已经支付
            return PayStatusEnum.SUCCESS;
        }

        return payHelper.queryPayState(id);
    }
}
