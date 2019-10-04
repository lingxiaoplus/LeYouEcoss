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
    BRAND_SAVE_ERROR(500,"新增品牌失败"),
    SELECT_GROUP_NOT_FOUNT(404,"商品规格组没有找到"),
    SELECT_GROUP_PARAM_NOT_FOUNT(404,"商品规格组参数没有找到"),
    GROUP_SAVE_ERROR(500,"商品规格组没有找到"),
    UPLOAD_FILE_ERROR(500,"文件上传失败"),
    FILE_TYPE_NOT_SUPPORT(500,"文件格式不支持"),
    ILLEGA_ARGUMENT(500,"传递过来的参数不正确")
    ;
    private int code;
    private String msg;
}
