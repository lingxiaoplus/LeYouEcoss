package com.lingxiao.jwt;


import com.lingxiao.pojo.UserInfo;
import com.lingxiao.utlis.JwtUtils;
import com.lingxiao.utlis.RsaUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.PrivateKey;
import java.security.PublicKey;

@RunWith(SpringRunner.class)
//@SpringBootTest
public class JwtTest {
    private static final String publicKeyPath = "D:\\rsakey\\rsa.pub";
    private static final String privateKeyPath = "D:\\rsakey\\rsa.pri";

    private PublicKey publicKey;
    private PrivateKey privateKey;

    @Test
    public void testCreateRsa() throws Exception {
        RsaUtils.generateKey(publicKeyPath,privateKeyPath,"123");
    }

    @Before
    public void testGetRsa() throws Exception {
        publicKey = RsaUtils.getPublicKey(publicKeyPath);
        privateKey = RsaUtils.getPrivateKey(privateKeyPath);
    }

    @Test
    public void testGenerateToken() throws Exception {
        String token = JwtUtils.generateToken(new UserInfo(20L, "jack"), privateKey, 5);
        System.out.println("token = " + token);
    }

    @Test
    public void testParseToken() throws Exception {
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6MjAsInVzZXJuYW1lIjoiamFjayIsImV4cCI6MTU3MTAzODg5Nn0.HfHHZ_Tg3EpHjbiL9C8gODM-nSqsXKczhLL9ZOhejjy7FAaIvqijvz9T5Nc37UPy2ewsQ7-9Ta5LeeRLKgcnQHzDn8weeqoO7Gi_Tzm8nFiMMX_LWM3ttuMSkYrB0u4RX_S1XvKp45T4fbEY35NWQsnHO30SXy06LvmNXTIQW3M";
        UserInfo userInfo = JwtUtils.getInfoFromToken(token, publicKey);
        System.out.println("user id: "+userInfo.getId());
        System.out.println("user name: "+userInfo.getUsername());
    }

}