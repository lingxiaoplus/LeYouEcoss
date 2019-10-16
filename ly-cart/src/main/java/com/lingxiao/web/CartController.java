package com.lingxiao.web;

import com.lingxiao.pojo.Cart;
import com.lingxiao.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("cartController")
public class CartController {
    @Autowired
    private CartService cartService;

    @PostMapping
    public ResponseEntity<Void> addCarts(@RequestBody Cart cart){
        cartService.addCarts(cart);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<Cart>> getCarts(){
        return ResponseEntity.ok(cartService.getCarts());
    }

    @PutMapping
    public ResponseEntity<Void> updateCart(@RequestBody Cart cart){
        cartService.updateCart(cart);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{skuId}")
    public ResponseEntity<Void> deleteCart(@PathVariable("skuId") Long skuId){
        cartService.deleteCart(skuId);
        return ResponseEntity.ok().build();
    }
}
