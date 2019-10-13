package com.lingxiao.sms.mq;

import com.lingxiao.common.JsonUtils;
import com.lingxiao.sms.SmsUtil;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Map;

@Component
public class SendMessageListener {
    @Autowired
    private SmsUtil smsUtil;
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(name = "sms.verify.code.queue",durable = "true"),
                    exchange = @Exchange(name = "ly.sms.exchange",type = ExchangeTypes.TOPIC),
                    key = {"sms.verify.code"}
            )
    )
    public void listenerInsertOrUpdate(Map<String,String> map){
        if (CollectionUtils.isEmpty(map)){
            return;
        }
        String phone = map.remove("phone");
        smsUtil.sendSms(phone, JsonUtils.serialize(map));
    }
}
