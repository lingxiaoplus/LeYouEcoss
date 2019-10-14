package com.lingxiao.web;

import com.lingxiao.pojo.User;
import com.lingxiao.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
    public ResponseEntity<Void> register(@Valid User user, String code){
        boolean registed = userService.register(user, code);
        if (!registed){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/query")
    public ResponseEntity<User> queryUserInfo(@RequestParam(name = "username") String username,
                                              @RequestParam(name = "password") String password){
        User user = userService.queryUserInfo(username,password);
        return ResponseEntity.ok(user);
    }
}
