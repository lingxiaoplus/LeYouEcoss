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

import java.util.List;

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
        Boolean hasKey = hashOperations.hasKey(skuId);
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
        BoundHashOperations<String, Object, Object> hashOperations = redisTemplate.boundHashOps(key);
        if (hashOperations.hasKey(key)){
            return null;
        }

        return null;
    }
}
