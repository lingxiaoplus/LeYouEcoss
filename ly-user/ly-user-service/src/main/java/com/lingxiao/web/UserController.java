package com.lingxiao.web;

import com.lingxiao.pojo.User;
import com.lingxiao.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller("userController")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/check/{data}/{type}")
    private ResponseEntity<Boolean> checkUserInfo(@PathVariable("data") String data, @PathVariable("type") Integer type){
        return ResponseEntity.ok(userService.checkUserInfo(data,type));
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendVerifyCode(String phone){
        return ResponseEntity.ok(userService.sendVerifyCode(phone));
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(String username,String password,String phone,String code){
        userService.register(username,password,phone,code);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
