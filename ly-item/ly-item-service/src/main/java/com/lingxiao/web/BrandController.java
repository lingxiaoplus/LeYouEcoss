package com.lingxiao.web;

import com.lingxiao.pojo.Brand;
import com.lingxiao.service.BrandService;
import com.lingxiao.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("brandController")
@RequestMapping("/brand")
public class BrandController {
    @Autowired
    private BrandService brandService;

    @GetMapping("/page")
    public ResponseEntity<PageResult<Brand>> getBrandByPage(
            @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,
            @RequestParam(value = "rows",defaultValue = "5") Integer rows,
            @RequestParam(value = "sortBy",defaultValue = "") String sortBy,
            @RequestParam(value = "desc",defaultValue = "false") Boolean desc,
            @RequestParam(value = "key",required = false) String key
    ){
        return ResponseEntity.ok(brandService.getBrandByPage(pageNum,rows,sortBy,desc,key));
    }

    @PostMapping
    public ResponseEntity<Void> addBrand(Brand brand, @RequestParam(value = "cids") List<Long> cids){
        brandService.addBrand(brand,cids);
        return ResponseEntity.ok().build();
    }
}
