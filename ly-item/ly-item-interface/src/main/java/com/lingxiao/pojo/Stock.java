package com.lingxiao.pojo;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "tb_stock")
@Data
public class Stock {
    @Id
    private Long sku_id;
    private Integer seckillStock;
    private Integer seckillTotal;
    private Integer stock;
}
