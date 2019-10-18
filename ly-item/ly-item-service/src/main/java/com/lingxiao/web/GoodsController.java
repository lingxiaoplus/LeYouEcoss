package com.lingxiao.web;

import com.lingxiao.dto.CartDto;
import com.lingxiao.pojo.Sku;
import com.lingxiao.pojo.Spu;
import com.lingxiao.pojo.SpuDetail;
import com.lingxiao.service.GoodsService;
import com.lingxiao.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("goodsController")
public class GoodsController {
    @Autowired
    private GoodsService goodsService;

    @GetMapping("/spu/page")
    public ResponseEntity<PageResult<Spu>> getGoodsByPage(
            @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,
            @RequestParam(value = "rows",defaultValue = "5") Integer rows,
            @RequestParam(value = "saleable",required = false) Boolean saleable,
            @RequestParam(value = "key",required = false) String key
    ){
        return ResponseEntity.ok(goodsService.getGoodsByPage(pageNum,rows,saleable,key));
    }

    @PostMapping("/goods")
    public ResponseEntity<Void> saveGoods(@RequestBody Spu spu){
        goodsService.saveGoods(spu);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/spu/detail/{id}")
    public ResponseEntity<SpuDetail> getGoodsDetail(@PathVariable("id") Long id){
        return ResponseEntity.ok(goodsService.getGoodsDetail(id));
    }

    @GetMapping("/sku/list/{sPuId}")
    public ResponseEntity<List<Sku>> getSkusByPid(@PathVariable("sPuId") Long id){
        return ResponseEntity.ok(goodsService.getSkusByPid(id));
    }

    /**
     * 查询spu
     * @param id
     * @return
     */
    @GetMapping("/spu/{id}")
    public ResponseEntity<Spu> getSpuById(@PathVariable("id") Long id){
        return ResponseEntity.ok(goodsService.getSpuById(id));
    }

    /**
     * 查询sku
     * @param id
     * @return
     */
    @GetMapping("/sku/{id}")
    public ResponseEntity<Sku> getSkuById(@PathVariable("id") Long id){
        return ResponseEntity.ok(goodsService.getSkuById(id));
    }

    /**
     * 查询skuList
     * @param ids
     * @return
     */
    @GetMapping("/sku/{ids}")
    public ResponseEntity<List<Sku>> getSkuListByIds(@PathVariable("ids") List<Long> ids){
        return ResponseEntity.ok(goodsService.getSkuListByIds(ids));
    }

    /**
     * 减少库存
     * @return
     */
    @PostMapping("/stock/decrease")
    public ResponseEntity<Void> decreaseStock(@RequestBody List<CartDto> cartList){
        goodsService.decreaseStock(cartList);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
