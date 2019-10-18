package com.lingxiao.service;

import com.lingxiao.client.AddressClient;
import com.lingxiao.client.GoodsClient;
import com.lingxiao.common.IdWorker;
import com.lingxiao.dto.AddressDto;
import com.lingxiao.dto.CartDto;
import com.lingxiao.dto.OrderDto;
import com.lingxiao.enums.ExceptionEnum;
import com.lingxiao.enums.OrderStatusEnum;
import com.lingxiao.exception.LyException;
import com.lingxiao.interceptor.LoginInterceptor;
import com.lingxiao.mapper.OrderDetailMapper;
import com.lingxiao.mapper.OrderMapper;
import com.lingxiao.mapper.OrderStatusMapper;
import com.lingxiao.pojo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        order.setUserId(userInfo.getId().toString());
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
        order.setActualPay(totalPrice - order.getPostFee() - 0);

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
}
