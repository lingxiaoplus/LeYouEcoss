package com.lingxiao.client;

import com.lingxiao.dto.AddressDto;

import java.util.ArrayList;
import java.util.List;

public abstract class AddressClient {
    public static final List<AddressDto> addressList = new ArrayList<AddressDto>(){
        {
            AddressDto address = new AddressDto();
            address.setId(1L);
            address.setName("胡歌");
            address.setState("上海");
            address.setCity("上海");
            address.setDistrict("浦东新区");
            address.setAddress("航头镇 航头路223号");
            address.setPhone("13000000000");
            address.setIsDefault(false);
            address.setZipCode("23000");

            add(address);
            AddressDto address1 = new AddressDto();
            address1.setId(2L);
            address1.setName("张三");
            address1.setState("北京");
            address1.setCity("北京");
            address1.setDistrict("浦东新区");
            address1.setAddress("航头镇 航头路223号");
            address1.setPhone("15000000000");
            address1.setIsDefault(false);
            address1.setZipCode("13000");
            add(address1);
        }
    };

    public static AddressDto findById(Long id){
        for (AddressDto address: addressList) {
            if (id == address.getId()){
                return address;
            }
        }
        return null;
    }
}
