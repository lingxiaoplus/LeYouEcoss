package com.lingxiao.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lingxiao.enums.ExceptionEnum;
import com.lingxiao.exception.LyException;
import com.lingxiao.mapper.BrandMapper;
import com.lingxiao.pojo.Brand;
import com.lingxiao.vo.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service("brandService")
@Slf4j
public class BrandService {

    @Autowired
    private BrandMapper brandMapper;

    public PageResult<Brand> getBrandByPage(Integer pageNum, Integer rows,
                                                            String sortBy, Boolean desc,
                                                            String key) {
        //分页查询数据
        PageHelper.startPage(pageNum,rows);
        //过滤条件
        Example example = new Example(Brand.class);
        if (StringUtils.isNotBlank(key)){
            //相当于sql语句 select * from tb_brand where name like %key% or letter = "letter"
            example.createCriteria().orLike("name","%"+key+"%")
                    .andEqualTo("letter",key.toUpperCase());
        }

        //排序  order by name desc/asc
        if (StringUtils.isNotBlank(sortBy)){
            String orderByClause = sortBy + (desc?" DESC":" ASC");
            example.setOrderByClause(orderByClause);
        }
        //查询
        List<Brand> list = brandMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(list)){
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        PageInfo pageInfo = new PageInfo(list);
        return new PageResult<Brand>(pageInfo.getTotal(),list);
    }

    @Transactional
    public void addBrand(Brand brand, List<Long> cids) {
        int count = brandMapper.insert(brand);
        if (count != 1){
            throw new LyException(ExceptionEnum.BRAND_SAVE_ERROR);
        }
        //新增中间表 这个地方自己控制，没有做外键关联，因为做外键关联之后数据库的性能会下降
        cids.forEach((id->{
            int row = brandMapper.insertCategoryBrand(id, brand.getId());
            if (row != 1){
                throw new LyException(ExceptionEnum.BRAND_SAVE_ERROR);
            }
        }));
    }

    public Brand getBrandById(Long id){
        Brand brand = brandMapper.selectByPrimaryKey(id);
        if (brand == null){
            log.error("品牌没有发现",id);
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return brand;
    }

    public List<Brand> getBrandByCid(Long cid) {
        List<Brand> list = brandMapper.selectBrandsByCid(cid);
        if (CollectionUtils.isEmpty(list)){
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return list;
    }

    /**
     * 根据bids 批量查询品牌
     * @param ids
     * @return
     */
    public List<Brand> getBrandByIds(List<Long> ids) {
        List<Brand> brandList = brandMapper.selectByIdList(ids);
        if (CollectionUtils.isEmpty(brandList)){
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return brandList;
    }
}
