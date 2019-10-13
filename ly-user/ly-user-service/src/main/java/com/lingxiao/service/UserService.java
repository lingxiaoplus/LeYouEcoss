package com.lingxiao.service;

import com.lingxiao.common.NumberUtils;
import com.lingxiao.enums.ExceptionEnum;
import com.lingxiao.exception.LyException;
import com.lingxiao.mapper.UserMapper;
import com.lingxiao.pojo.User;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service("userService")
public class UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AmqpTemplate amqpTemplate;
    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String KEY_PREFIX = "user:code:phone:";
    /**
     * 校验用户填写的信息
     * @param data
     * @param type
     * @return
     */
    public boolean checkUserInfo(String data, Integer type) {
        User user = new User();
        switch (type){
            case 1:
                user.setUsername(data);
                break;
            case 2:
                user.setPhone(data);
                break;
            default:
                throw new LyException(ExceptionEnum.ILLEGA_ARGUMENT);
        }
        int count = userMapper.selectCount(user);
        if (count > 0){
            return false;
        }
        return true;
    }

    public String sendVerifyCode(String phone) {
        if (StringUtils.isBlank(phone) && phone.trim().length() != 11){
            throw new LyException(ExceptionEnum.ILLEGA_ARGUMENT);
        }
        Map<String, String> map = new HashMap<>();
        map.put("phone",phone);
        String generateCode = NumberUtils.generateCode(6);
        map.put("code", generateCode);

        amqpTemplate.convertAndSend("ly.sms.exchange","sms.verify.code",map);
        //将验证码保存5分钟
        redisTemplate.opsForValue().set(KEY_PREFIX+phone,generateCode,5, TimeUnit.MINUTES);
        return generateCode;
    }

    public boolean register(String username, String password, String phone, String code) {
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)
                || StringUtils.isBlank(phone) || StringUtils.isBlank(code)){
            throw new LyException(ExceptionEnum.ILLEGA_ARGUMENT);
        }
        String cacheCode = redisTemplate.opsForValue().get(KEY_PREFIX + phone);
        if (StringUtils.isBlank(cacheCode) || !cacheCode.equals(code)){
            return false;
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setPhone(phone);
        user.setCreated(new Date());
        //user.setSalt();
        int insert = userMapper.insert(user);
        if (insert != 1){
            return false;
        }
        // 注册成功，删除redis中的记录
        redisTemplate.delete(KEY_PREFIX + phone);
        return true;
    }
}
