package com.lingxiao.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {
    private Long id;
    private String name;
    private String phone;
    private String state;  //省
    private String city;  //城市
    private String district; //区
    private String address; //街道地址
    private String zipCode; //邮编
    private Boolean isDefault;
}
