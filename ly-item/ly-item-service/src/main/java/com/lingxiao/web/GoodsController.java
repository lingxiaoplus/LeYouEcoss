package com.lingxiao.web;

import com.lingxiao.pojo.Spu;
import com.lingxiao.service.GoodsService;
import com.lingxiao.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("goodsController")
@RequestMapping("/spu")
public class GoodsController {
    @Autowired
    private GoodsService goodsService;

    @GetMapping("/page")
    public ResponseEntity<PageResult<Spu>> getGoodsByPage(
            @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,
            @RequestParam(value = "rows",defaultValue = "5") Integer rows,
            @RequestParam(value = "saleable",required = false) Boolean saleable,
            @RequestParam(value = "key",required = false) String key
    ){
        return ResponseEntity.ok(goodsService.getGoodsByPage(pageNum,rows,saleable,key));
    }
}
