package com.lingxiao.search.mq;

import com.lingxiao.search.service.SearchService;
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
    private SearchService searchService;
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(name = "search.item.insert.queue",durable = "true"),
                    exchange = @Exchange(name = "ly.item.exchange",type = ExchangeTypes.TOPIC),
                    key = {"item.insert","item.update"}
            )
    )
    public void listenerInsertOrUpdate(Long spuId){
        if (spuId == null){
            return;
        }
        //新建或者更新索引库
        searchService.createOrUpdateIndexs(spuId);
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(name = "search.item.delete.queue",durable = "true"),
                    exchange = @Exchange(name = "ly.item.exchange",type = ExchangeTypes.TOPIC),
                    key = {"item.delete"}
            )
    )
    public void listenerDelete(Long spuId){
        if (spuId == null){
            return;
        }
        //新建或者更新索引库
        searchService.deleteIndexs(spuId);
    }
}
