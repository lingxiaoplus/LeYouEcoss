package com.lingxiao.web;

import com.lingxiao.pojo.Brand;
import com.lingxiao.service.BrandService;
import com.lingxiao.vo.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("brandController")
@RequestMapping("/brand")
@Api("品牌管理接口")
public class BrandController {
    @Autowired
    private BrandService brandService;

    @GetMapping("/page")
    @ApiOperation(value = "分页查询当前品牌列表，并且可以根据关键词过滤", notes = "分页查询当前品牌列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum",value = "当前页",defaultValue = "1",type = "Integer"),
            @ApiImplicitParam(name = "rows",value = "每页个数",defaultValue = "5",type = "Integer"),
            @ApiImplicitParam(name = "sortBy",value = "按照哪个字段排序",type = "String"),
            @ApiImplicitParam(name = "desc",value = "是否升序",type = "Boolean"),
            @ApiImplicitParam(name = "key",value = "关键字",type = "String"),
    })
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
    @ApiOperation(value = "添加品牌，没有返回值",notes = "添加品牌")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "brand",value = "品牌对象"),
            @ApiImplicitParam(name = "cids",value = "分别是三级分类的id")
    })
    public ResponseEntity<Void> addBrand(Brand brand, @RequestParam(value = "cids") List<Long> cids){
        brandService.addBrand(brand,cids);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/cid/{cid}")
    @ApiOperation(value = "通过第三级分类的id获取品牌",notes = "查询品牌")
    @ApiImplicitParam(name = "cid",value = "第三级分类的id")
    public ResponseEntity<List<Brand>> getBrandByCid(@PathVariable("cid") Long cid){
        return ResponseEntity.ok(brandService.getBrandByCid(cid));
    }

    /**
     * 根据品牌id查到品牌
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "通过id获取品牌",notes = "查询品牌")
    @ApiImplicitParam(name = "id",value = "品牌id")
    public ResponseEntity<Brand> getBrandById(@PathVariable("id") Long id){
        return ResponseEntity.ok(brandService.getBrandById(id));
    }

    @GetMapping("/list")
    @ApiOperation(value = "通过一组id获取一组品牌",notes = "批量查询品牌")
    @ApiImplicitParam(name = "ids",value = "一组品牌id")
    public ResponseEntity<List<Brand>> getBrandByIds(@RequestParam("ids") List<Long> ids){
        return ResponseEntity.ok(brandService.getBrandByIds(ids));
    }
}
