package com.lingxiao.pojo;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "tb_spu_detail")
@Data
public class SpuDetail {
    @Id
    private Long spu_id;
    private String description;
    private String packingList;
    private String afterService;

    private String genericSpec;
    private String specialSpec;
}
