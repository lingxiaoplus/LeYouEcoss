package com.lingxiao.service;

import com.lingxiao.client.AddressClient;
import com.lingxiao.common.IdWorker;
import com.lingxiao.config.IdWorkerConfig;
import com.lingxiao.dto.AddressDto;
import com.lingxiao.dto.CartDto;
import com.lingxiao.dto.OrderDto;
import com.lingxiao.enums.ExceptionEnum;
import com.lingxiao.exception.LyException;
import com.lingxiao.interceptor.LoginInterceptor;
import com.lingxiao.mapper.OrderDetailMapper;
import com.lingxiao.mapper.OrderMapper;
import com.lingxiao.mapper.OrderStatusMapper;
import com.lingxiao.pojo.Order;
import com.lingxiao.pojo.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service("orderService")
public class OrderService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper detailMapper;
    @Autowired
    private OrderStatusMapper statusMapper;
    @Autowired
    private IdWorker idWorker;

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
        List<Long> skuIds = dto
                .getCarts()
                .stream()
                .map(CartDto::getSkuId)
                .collect(Collectors.toList());

        //2.创建订单详情

        //3.创建订单状态

        //4.减库存
        return null;
    }
}
