package com.lingxiao.web;

import com.lingxiao.pojo.Category;
import com.lingxiao.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("categoryController")
@RequestMapping("/category")
@Api("分类管理接口")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list")
    @ApiOperation(value = "查询分类，返回值为该分类下的所有子分类",notes = "查询分类")
    @ApiImplicitParam(name = "pid",value = "分类的父id")
    public ResponseEntity<List<Category>> queryCategoryListByPid(
            @RequestParam("pid")Long pid
            ){
        return ResponseEntity.ok(categoryService.queryCategoryListByPid(pid));
    }

    /**
     * 通过分类的id查到具体的分类
     * @param ids
     * @return
     */
    @GetMapping("/list/ids")
    @ApiOperation(value = "查询分类，返回值为多个分类详情",notes = "查询分类")
    @ApiImplicitParam(name = "ids",value = "一组分类id")
    public ResponseEntity<List<Category>> queryCategoryNamesByIds(@RequestParam("ids") List<Long> ids){
        return ResponseEntity.ok(categoryService.queryCategoryListByIds(ids));
    }
}
