package com.lingxiao.pojo;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "tb_category")
@Data
public class Category {
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;
    private String name;
    private Long parentId;

    private Boolean isParent;   //不加@Data，自己写setget方法，传给前端的字段为parent
    private Integer sort;

}
