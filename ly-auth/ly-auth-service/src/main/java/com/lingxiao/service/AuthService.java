package com.lingxiao.service;

import com.lingxiao.client.UserClient;
import com.lingxiao.config.JwtProperties;
import com.lingxiao.enums.ExceptionEnum;
import com.lingxiao.exception.LyException;
import com.lingxiao.pojo.User;
import com.lingxiao.pojo.UserInfo;
import com.lingxiao.utlis.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

@Service("authService")
@EnableConfigurationProperties(JwtProperties.class)
@Slf4j
public class AuthService {
    @Autowired
    private UserClient userClient;
    @Autowired
    private JwtProperties jwtProperties;

    public String authEntication(String username, String password) {
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)){
            throw new LyException(ExceptionEnum.ILLEGA_ARGUMENT);
        }
        User user = userClient.queryUserInfo(username, password);
        if (user == null){
            return null;
        }
        UserInfo userInfo = new UserInfo(user.getId(), user.getUsername());
        try {
            String generateToken = JwtUtils.generateToken(userInfo, jwtProperties.getPrivateKey(), jwtProperties.getExpire());
            return generateToken;
        } catch (Exception e) {
            log.error("生成token失败 ",e);
        }
        return null;
    }
}
