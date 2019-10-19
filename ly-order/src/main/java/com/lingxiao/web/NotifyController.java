package com.lingxiao.web;

import com.lingxiao.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/notify")
@Slf4j
public class NotifyController {

    @Autowired
    private OrderService orderService;
    @PostMapping(value = "/pay",produces = "application/xml")
    public Map<String,String> notifyPayResult(@RequestBody Map<String,String> result){
        log.info("微信支付回调成功");
        orderService.handlerNotify(result);
        //返回给微信的消息
        Map<String,String> map = new HashMap();
        map.put("return_code","SUCCESS");
        map.put("return_msg","OK");
        return map;
    }
}
