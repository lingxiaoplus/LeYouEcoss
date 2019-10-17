package com.lingxiao.pojo;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

@Table(name = "tb_order")
@Data
public class Order {
    @Id
    private Long orderId;
    private Long totalPay;
    private Long actualPay;
    private String promotionIds;
    private Integer paymentType;
    private Long postFee;
    private Date createTime;
    private String shippingName;
    private String shippingCode;
    private String userId;
    private String buyerMessage;
    private String buyerNick;
    private String buyerRate;

    private String receiverState;
    private String receiverCity;
    private String receiverDistrict;
    private String receiverAddress;
    private String receiverMobile;
    private String receiverZip;
    private String receiver;
    private Integer invoiceType;
    private Integer sourceType;

    @Transient
    private OrderStatus orderStatus;

    @Transient
    private List<OrderDetail> orderDetails;

}
