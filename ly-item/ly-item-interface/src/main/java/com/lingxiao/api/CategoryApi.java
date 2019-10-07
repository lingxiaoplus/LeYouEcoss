package com.lingxiao.api;

import com.lingxiao.pojo.Category;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface CategoryApi {
    @GetMapping("/category/list/ids")
    List<Category> queryCategoryNamesByIds(@RequestParam("ids") List<Long> ids);
}
