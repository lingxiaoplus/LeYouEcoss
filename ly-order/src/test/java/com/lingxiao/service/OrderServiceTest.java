package com.lingxiao.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;
@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderServiceTest {

    @Autowired
    private OrderService orderService;
    @Test
    public void createPayUrl() {
        String payUrl = orderService.createPayUrl(1185564377844158464L);
        System.out.println("支付二维码链接："+payUrl);
    }
}