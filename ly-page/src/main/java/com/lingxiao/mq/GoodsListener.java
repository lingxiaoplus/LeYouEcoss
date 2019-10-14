package com.lingxiao.mq;

import com.lingxiao.service.PageService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GoodsListener {

    @Autowired
    private PageService pageService;
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(name = "page.item.insert.queue",durable = "true"),
                    exchange = @Exchange(name = "ly.item.exchange",type = ExchangeTypes.TOPIC),
                    key = {"item.insert","item.update"}
            )
    )
    public void listenerInsertOrUpdate(Long spuId){
        if (spuId == null){
            return;
        }
        System.out.println("新建静态页面");
        //新建静态页面
        pageService.createHtml(spuId);
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(name = "page.item.delete.queue",durable = "true"),
                    exchange = @Exchange(name = "ly.item.exchange",type = ExchangeTypes.TOPIC),
                    key = {"item.delete"}
            )
    )
    public void listenerDelete(Long spuId){
        if (spuId == null){
            return;
        }
        System.out.println("删除静态页面");
        pageService.deleteHtml(spuId);
    }
}
