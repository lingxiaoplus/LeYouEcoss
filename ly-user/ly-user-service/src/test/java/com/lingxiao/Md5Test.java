package com.lingxiao;

import com.lingxiao.common.MD5Util;
import org.apache.commons.codec.CharEncoding;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Md5Test {
    
    @Test
    public void createPassword() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        byte[] salt = MD5Util.createSalt();
        String saltStr =  MD5Util.byteToHexString(salt);
        String encryptedPwd = MD5Util.getEncryptedPwd("123456", salt);
        boolean equal = MD5Util.validPassword("123456", encryptedPwd,salt);
        System.out.println("equal = " + equal + "  saltStr = " + saltStr);
    }
}
