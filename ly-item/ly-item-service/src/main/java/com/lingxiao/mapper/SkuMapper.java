package com.lingxiao.mapper;

import com.lingxiao.pojo.Sku;
import tk.mybatis.mapper.additional.idlist.IdListMapper;
import tk.mybatis.mapper.common.Mapper;

public interface SkuMapper extends Mapper<Sku> , IdListMapper<Sku,Long> {
}
