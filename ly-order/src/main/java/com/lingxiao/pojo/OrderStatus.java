package com.lingxiao.pojo;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "tb_order_status")
@Data
public class OrderStatus {
    @Id
    private Long orderId;
    private Integer status;
    private Date createTime;
    private Date paymentTime;
    private Date consignTime;
    private Date endTime;
    private Date closeTime;
    private Date commentTime;
}
