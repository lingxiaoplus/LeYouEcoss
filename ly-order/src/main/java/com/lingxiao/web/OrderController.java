package com.lingxiao.web;

import com.lingxiao.dto.OrderDto;
import com.lingxiao.pojo.Order;
import com.lingxiao.pojo.OrderStatus;
import com.lingxiao.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller("orderController")
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<Long> createOrder(@RequestBody OrderDto dto){
        return ResponseEntity.ok(orderService.createOrder(dto));
    }

    /**
     * 通过订单号查询id
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<Order> queryOrderById(@PathVariable("id") Long id){
        return ResponseEntity.ok(orderService.queryOrderById(id));
    }
    /**
     * 通过订单id生成订单
     * @param id
     * @return
     */
    @GetMapping("/url/{id}")
    public ResponseEntity<String> createPayUrl(@PathVariable("id") Long id){
        return ResponseEntity.ok(orderService.createPayUrl(id));
    }

    @GetMapping("/status/{id}")
    public ResponseEntity<Integer> queryStatus(@PathVariable("id") Long id){
        return ResponseEntity.ok(orderService.queryOrderStatus(id).getValue());
    }
}
