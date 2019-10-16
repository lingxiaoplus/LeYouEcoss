package com.lingxiao.service;

import com.lingxiao.client.GoodsClient;
import com.lingxiao.common.JsonUtils;
import com.lingxiao.enums.ExceptionEnum;
import com.lingxiao.exception.LyException;
import com.lingxiao.interceptor.LoginInterceptor;
import com.lingxiao.pojo.Cart;
import com.lingxiao.pojo.Sku;
import com.lingxiao.pojo.UserInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service("cartService")
public class CartService {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private GoodsClient goodsClient;

    private static final String KEY_PREFIX = "ly:cart:uid:";
    public void addCarts(Cart cart) {
        if (cart == null){
            throw new LyException(ExceptionEnum.ILLEGA_ARGUMENT);
        }
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        String key = KEY_PREFIX + userInfo.getId();
        //获取hash操作对象
        BoundHashOperations<String, Object, Object> hashOperations = redisTemplate.boundHashOps(key);

        Long skuId = cart.getSkuId();
        Integer cartNum = cart.getNum();
        //判断是否存在这个key
        Boolean hasKey = hashOperations.hasKey(skuId.toString());
        if (hasKey){
            //说明购物车中有这个商品了
            String jsonCart = hashOperations.get(skuId.toString()).toString();
            cart = JsonUtils.parse(jsonCart,Cart.class);
            cart.setNum(cart.getNum() + cartNum);
        }else {
            Sku sku = goodsClient.getSkuById(skuId);
            cart.setUserId(userInfo.getId());
            cart.setPrice(sku.getPrice());
            cart.setImage(StringUtils.substringBefore(sku.getImages(),","));
            cart.setOwnSpec(sku.getOwnSpec());
            cart.setTitle(sku.getTitle());
        }
        //将数据写入购物车
        hashOperations.put(cart.getSkuId().toString(),JsonUtils.serialize(cart));
    }

    public List<Cart> getCarts() {
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        String key = KEY_PREFIX + userInfo.getId();
        if (!redisTemplate.hasKey(key)){
            return null;
        }
        BoundHashOperations<String, Object, Object> hashOperations = redisTemplate.boundHashOps(key);

        List<Object> jsonObjects = hashOperations.values();
        if (CollectionUtils.isEmpty(jsonObjects)){
            return null;
        }
        List<Cart> carts = jsonObjects.stream()
                .map(o -> JsonUtils.parse(o.toString(), Cart.class))
                .collect(Collectors.toList());
        return carts;
    }

    public void updateCart(Cart cart) {
        if (cart == null){
            throw new LyException(ExceptionEnum.ILLEGA_ARGUMENT);
        }
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        String key = KEY_PREFIX + userInfo.getId();
        BoundHashOperations<String, Object, Object> hashOperations = redisTemplate.boundHashOps(key);

        String skuId = cart.getSkuId().toString();
        if (!hashOperations.hasKey(skuId)){
            throw new LyException(ExceptionEnum.CART_NOT_FOUND);
        }
        Object o = hashOperations.get(skuId);
        Cart parse = JsonUtils.parse(o.toString(), Cart.class);
        parse.setNum(cart.getNum());
        hashOperations.put(cart.getSkuId().toString(),JsonUtils.serialize(parse));
    }

    public void deleteCart(Long skuId) {
        if (skuId == null){
            throw new LyException(ExceptionEnum.ILLEGA_ARGUMENT);
        }
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        String key = KEY_PREFIX + userInfo.getId();
        BoundHashOperations<String, Object, Object> hashOperations = redisTemplate.boundHashOps(key);
        Long deleteCount = hashOperations.delete(skuId.toString());
        if (deleteCount < 1){
            throw new LyException(ExceptionEnum.CART_NOT_FOUND);
        }
    }
}
