package com.lingxiao.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum  ExceptionEnum {
    BRAND_NOT_FOUND(404,"品牌没有发现"),
    CATEGORY_LIST_IS_EMPTY(404,"商品分类为空"),
    GOODS_LIST_IS_EMPTY(404,"商品列表为空"),
    GOODS_SPU_ADD_ERROR(500,"新增商品spu失败"),
    GOODS_SKU_ADD_ERROR(500,"新增商品sku失败"),
    GOODS_SPU_DETAIL_ADD_ERROR(500,"新增商品详情失败"),
    GOODS_STOCK_ADD_ERROR(500,"新增商品库存失败"),
    GOODS_SPU_ID_NULL_ERROR(500,"商品spu的id不能为空"),
    GOODS_STOCK_IS_EMPTY(404,"商品库存不存在"),
    GOODS_DETAIL_NOT_EXIST(404,"商品详情不存在"),
    GOODS_SKU_LIST_NOT_EXIST(404,"商品sku列表不存在"),
    BRAND_SAVE_ERROR(500,"新增品牌失败"),
    SELECT_GROUP_NOT_FOUNT(404,"商品规格组没有找到"),
    SELECT_GROUP_PARAM_NOT_FOUNT(404,"商品规格组参数没有找到"),
    GROUP_SAVE_ERROR(500,"商品规格组没有找到"),
    UPLOAD_FILE_ERROR(500,"文件上传失败"),
    FILE_TYPE_NOT_SUPPORT(500,"文件格式不支持"),
    ILLEGA_ARGUMENT(500,"传递过来的参数不正确"),
    ILLEGA_PHONE_VERIFY_CODE(500,"验证码不正确"),
    USER_REGIST_ERROR(500,"用户注册失败"),
    CART_NOT_FOUND(500,"购物车商品不存在"),
    CREATE_ORDER_ERROE(500,"创建订单失败"),
    ORDER_NOT_FOUND(404,"订单不存在"),
    ORDER_DETAIL_NOT_FOUND(404,"订单详情不存在"),
    ORDER_STATUS_NOT_FOUND(404,"订单状态不存在"),
    STOCK_NOT_ENOUGH(500,"库存不足"),
    WX_PAY_ORDER_FAIL(500,"微信下单失败"),
    ORDER_STATUS_ERROR(400,"订单状态异常"),
    ILLEGA_PAY_SIGN(400,"非法签名"),
    INVALID_ORDER_PARAM(400,"订单参数异常"),
    UPDATE_ORDER_STATUS_ERROR(500,"修改订单状态失败"),
    ;
    private int code;
    private String msg;
}
