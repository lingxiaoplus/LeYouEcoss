package com.lingxiao.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    @NotNull
    private Long addressId;  //收货人地址id
    @NotNull
    private Integer paymentType; //付款方式
    @NotNull
    private List<CartDto> carts;
}
