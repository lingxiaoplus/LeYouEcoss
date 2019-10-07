package com.lingxiao.api;

import com.lingxiao.pojo.Brand;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface BrandApi {
    @GetMapping("/brand/{id}")
    Brand getBrandById(@PathVariable("id") Long id);
}
