package com.lingxiao.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lingxiao.enums.ExceptionEnum;
import com.lingxiao.exception.LyException;
import com.lingxiao.mapper.*;
import com.lingxiao.pojo.*;
import com.lingxiao.vo.PageResult;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.beans.Transient;
import java.util.*;
import java.util.stream.Collectors;

@Service("goodsService")
public class GoodsService {
    @Autowired
    private SpuMapper spuMapper;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private SpuDetailMapper spuDetailMapper;
    @Autowired
    private StockMapper stockMapper;
    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

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

            Brand brand = brandService.getBrandById(spu.getBrandId());
            if (brand != null)
                spu.setBname(brand.getName());
        }
    }

    @Transactional
    public void saveGoods(Spu spu) {
        //新增spu
        spu.setCreateTime(new Date());
        spu.setLastUpdateTime(spu.getCreateTime());
        spu.setId(null);
        spu.setSaleable(true);
        spu.setValid(false);
        int spuCount = spuMapper.insert(spu);
        if (spuCount != 1){
            throw new LyException(ExceptionEnum.GOODS_SPU_ADD_ERROR);
        }
        //新增detail
        SpuDetail spuDetail = spu.getSpuDetail();
        spuDetail.setSpu_id(spu.getId());
        int detailCount = spuDetailMapper.insert(spuDetail);
        if (detailCount != 1){
            throw new LyException(ExceptionEnum.GOODS_SPU_DETAIL_ADD_ERROR);
        }
        //新增sku
        List<Sku> skus = spu.getSkus();
        List<Stock> stocks = new ArrayList<>();
        skus.stream().forEach((sku)->{
            sku.setSpuId(spu.getId());
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            int skuCount = skuMapper.insert(sku);
            if (skuCount != 1){
                throw new LyException(ExceptionEnum.GOODS_SKU_ADD_ERROR);
            }
            Stock stock = new Stock();
            stock.setSku_id(sku.getId());
            stock.setStock(sku.getStock());
            stocks.add(stock);
        });
        int stockCount = stockMapper.insertList(stocks);
        if (stockCount != stocks.size()){
            throw new LyException(ExceptionEnum.GOODS_STOCK_ADD_ERROR);
        }

        //发送rabbitmq消息
        amqpTemplate.convertAndSend("item.insert",spu.getId());
    }


    @Transactional
    public void updateGoods(Spu spu) {
        if (spu.getId() == null){
            throw new LyException(ExceptionEnum.GOODS_SPU_ID_NULL_ERROR);
        }

        Sku sku = new Sku();
        sku.setSpuId(spu.getId());
        List<Sku> skus = skuMapper.select(sku);
        if (!CollectionUtils.isEmpty(skus)){
            skuMapper.delete(sku);
            List<Long> ids = skus.stream().map(Sku::getId).collect(Collectors.toList());
            stockMapper.deleteByIdList(ids);
        }

        //spu的这些值不修改
        spu.setSaleable(null);
        spu.setValid(null);
        spu.setCreateTime(null);
        spu.setLastUpdateTime(new Date());


        int spuCount = spuMapper.updateByPrimaryKeySelective(spu);
        if (spuCount != 1){
            throw new LyException(ExceptionEnum.GOODS_SPU_ADD_ERROR);
        }

        int detailCount = spuDetailMapper.updateByPrimaryKeySelective(spu.getSpuDetail());
        if (detailCount != 1){
            throw new LyException(ExceptionEnum.GOODS_SPU_DETAIL_ADD_ERROR);
        }

        //新增sku和stock
        saveSkusAndStock(spu);
        //发送rabbitmq消息
        amqpTemplate.convertAndSend("item.update",spu.getId());
    }

    private void saveSkusAndStock(Spu spu) {

    }

    public SpuDetail getGoodsDetail(Long id) {
        SpuDetail spuDetail = spuDetailMapper.selectByPrimaryKey(id);
        if (spuDetail == null){
            // TODO: 2019/10/7 暂时去除  不然在添加elasticsearch索引库的时候会因为一些脏数据添加失败
            //throw new LyException(ExceptionEnum.GOODS_DETAIL_NOT_EXIST);
        }
        return spuDetail;
    }

    /**
     * 通过spu的id获取一组sku
     * @param id
     * @return
     */
    public List<Sku> getSkusByPid(Long id) {
        Sku sku = new Sku();
        sku.setSpuId(id);
        List<Sku> skuList = skuMapper.select(sku);
        if (CollectionUtils.isEmpty(skuList)){
            throw new LyException(ExceptionEnum.GOODS_SKU_LIST_NOT_EXIST);
        }

        //查询库存
        /*skuList.forEach((sk)->{
            Stock stock = stockMapper.selectByPrimaryKey(sk.getId());
            if (stock == null){
                throw new LyException(ExceptionEnum.GOODS_STOCK_IS_EMPTY);
            }
            sku.setStock(stock.getStock());
        });*/
        List<Long> ids = skuList.stream().map(Sku::getId).collect(Collectors.toList());
        List<Stock> stocks = stockMapper.selectByIdList(ids);
        if (CollectionUtils.isEmpty(stocks)){
            throw new LyException(ExceptionEnum.GOODS_STOCK_IS_EMPTY);
        }
        Map<Long, Integer> map = stocks.stream().collect(Collectors.toMap(Stock::getSku_id, Stock::getStock));
        stocks.forEach(stock -> stock.setStock(map.get(stock.getSku_id())));
        return skuList;
    }

    public Sku getSkuById(Long id){
        Sku sku = skuMapper.selectByPrimaryKey(id);
        if (sku == null){
            throw new LyException(ExceptionEnum.GOODS_SKU_LIST_NOT_EXIST);
        }
        return sku;
    }

    public Spu getSpuById(Long id) {
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if (spu == null){
            throw new LyException(ExceptionEnum.GOODS_DETAIL_NOT_EXIST);
        }
        //查询sku
        spu.setSkus(getSkusByPid(id));
        //查询detail
        spu.setSpuDetail(getGoodsDetail(id));
        return spu;
    }
}
