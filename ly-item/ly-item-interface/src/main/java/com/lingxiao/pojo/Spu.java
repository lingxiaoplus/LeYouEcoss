package com.lingxiao.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

@Table(name = "tb_spu")
@Data
public class Spu {
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;
    private Long brand_id;
    private Long cid1;
    private Long cid2;
    private Long cid3;
    private String title;
    private String sub_title;
    private Boolean saleable;
    @JsonIgnore
    private Boolean valid;  //用于逻辑删除
    private Date create_time;
    @JsonIgnore
    private Date last_update_time;

    @Transient  //忽略这两个字段
    private String cname;
    @Transient
    private String bname;
}
