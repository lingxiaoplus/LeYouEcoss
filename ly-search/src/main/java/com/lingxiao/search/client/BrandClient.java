package com.lingxiao.search.client;

import com.lingxiao.api.BrandApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "item-service")
public interface BrandClient extends BrandApi {
}
