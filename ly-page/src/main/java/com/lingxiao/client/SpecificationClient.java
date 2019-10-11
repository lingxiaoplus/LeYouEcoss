package com.lingxiao.client;

import com.lingxiao.api.SpecificationApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "item-service")
public interface SpecificationClient extends SpecificationApi {

}
