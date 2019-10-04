package com.lingxiao.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lingxiao.enums.ExceptionEnum;
import com.lingxiao.exception.LyException;
import com.lingxiao.mapper.BrandMapper;
import com.lingxiao.mapper.SpuMapper;
import com.lingxiao.pojo.Brand;
import com.lingxiao.pojo.Category;
import com.lingxiao.pojo.Spu;
import com.lingxiao.vo.PageResult;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service("goodsService")
public class GoodsService {
    @Autowired
    private SpuMapper spuMapper;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private BrandService brandService;

    public PageResult<Spu> getGoodsByPage(Integer pageNum, Integer rows, Boolean saleable, String key) {
        PageHelper.startPage(pageNum,rows);

        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(key)){
            criteria.andLike("title","%"+key+"%");
        }
        if (saleable != null){
            criteria.andEqualTo("saleable",saleable);
        }
        //默认排序
        example.setOrderByClause("last_update_time DESC");
        List<Spu> spuList = spuMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(spuList)){
            throw new LyException(ExceptionEnum.GOODS_LIST_IS_EMPTY);
        }
        loadCategoryNameAndBrandName(spuList);
        PageInfo<Spu> pageInfo = new PageInfo<>(spuList);
        return new PageResult(pageInfo.getTotal(),spuList);
    }

    private void loadCategoryNameAndBrandName(List<Spu> spuList) {
        for (Spu spu: spuList) {
            List<Category> categoryList = categoryService.queryCategoryListByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
            List<String> collect = categoryList.stream().map(Category::getName).collect(Collectors.toList());
            spu.setCname(StringUtils.join(collect,"/"));

            Brand brand = brandService.getBrandById(spu.getBrand_id());
            if (brand != null)
                spu.setBname(brand.getName());
        }
    }
}
