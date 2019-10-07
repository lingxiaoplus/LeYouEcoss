package com.lingxiao.search.client;

import com.lingxiao.api.CategoryApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "item-service")
public interface CategoryClient extends CategoryApi {
}
