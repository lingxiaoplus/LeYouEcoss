package com.lingxiao.search.client;

import com.lingxiao.api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "item-service")
public interface GoodsClient extends GoodsApi {

}
