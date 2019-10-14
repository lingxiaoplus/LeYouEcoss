package com.lingxiao.api;

import com.lingxiao.pojo.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

public interface UserApi {
    @GetMapping("/query")
    User queryUserInfo(@RequestParam(name = "username") String username,
                                              @RequestParam(name = "password") String password);
}
