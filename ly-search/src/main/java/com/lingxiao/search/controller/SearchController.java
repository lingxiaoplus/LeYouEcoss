package com.lingxiao.search.controller;

import com.lingxiao.search.pojo.Goods;
import com.lingxiao.search.pojo.SearchRequest;
import com.lingxiao.search.service.SearchService;
import com.lingxiao.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.awt.print.PrinterAbortException;

@RestController
//@RequestMapping("/search")
public class SearchController {
    @Autowired
    private SearchService searchService;

    @PostMapping("/page")
    public ResponseEntity<PageResult<Goods>> getGoodsList(@RequestBody SearchRequest searchRequest){
        return ResponseEntity.ok(searchService.getGoodsList(searchRequest));
    }
}
