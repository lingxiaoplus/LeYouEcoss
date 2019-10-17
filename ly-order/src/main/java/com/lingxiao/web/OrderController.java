package com.lingxiao.web;

import com.lingxiao.dto.OrderDto;
import com.lingxiao.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller("orderController")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<Long> createOrder(@RequestBody OrderDto dto){
        return ResponseEntity.ok(orderService.createOrder(dto));
    }
}
