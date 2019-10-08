package com.lingxiao.search.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.lingxiao.common.JsonUtils;
import com.lingxiao.pojo.*;
import com.lingxiao.search.client.BrandClient;
import com.lingxiao.search.client.CategoryClient;
import com.lingxiao.search.client.GoodsClient;
import com.lingxiao.search.client.SpecificationClient;
import com.lingxiao.search.pojo.Goods;
import com.lingxiao.search.pojo.SearchRequest;
import com.lingxiao.search.pojo.SearchResult;
import com.lingxiao.search.repository.GoodsRepository;
import com.lingxiao.vo.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.LongTermsAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SearchService {
    @Autowired
    private BrandClient brandClient;
    @Autowired
    private CategoryClient categoryClient;
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private SpecificationClient specificationClient;
    @Autowired
    private GoodsRepository goodsRepository;
    //聚合查询只能用下面这个template
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    public Goods createGoodsData(Spu spu){

        //搜索条件
        String searchText = getSearchText(spu);

        List<Sku> skuList = goodsClient.getSkusByPid(spu.getId());
        //价格集合
        Set<Long> priceSet = new HashSet<>();
        //sku
        List<Map<String,Object>> skus = new ArrayList<>();
        skuList.forEach((sku -> {
            Map<String,Object> map = new HashMap<>();
            map.put("id",sku.getId());
            map.put("title",sku.getTitle());
            map.put("price",sku.getPrice());
            map.put("image",StringUtils.substringBefore(sku.getImages(),","));
            skus.add(map);
            priceSet.add(sku.getPrice());
        }));

        //查询规格参数
        List<SpecParam> specParamList = specificationClient.
                getGroupParamByPid(null, spu.getCid3(), true);
        // TODO: 2019/10/7 暂时去除  不然在添加elasticsearch索引库的时候会因为一些脏数据添加失败
        if (specParamList == null){
            return null;
        }
        SpuDetail goodsDetail = goodsClient.getGoodsDetail(spu.getId());
        // TODO: 2019/10/7 暂时去除  不然在添加elasticsearch索引库的时候会因为一些脏数据添加失败
        if (goodsDetail == null){
            return null;
        }
        //获取通用参数  数据库里存的为json格式
        String genericSpec = goodsDetail.getGenericSpec();
        Map<Long, String> generMap = JsonUtils.parseMap(genericSpec, Long.class, String.class);
        //获取特定参数
        String specialSpec = goodsDetail.getSpecialSpec();
        Map<Long, List<String>> specialMap = JsonUtils.nativeRead(specialSpec, new TypeReference<Map<Long, List<String>>>() {
        });
        Map<String,Object> specMap = new HashMap<>();
        specParamList.forEach((specParam -> {
            String key = specParam.getName();
            Object value = "";
            if (specParam.getGeneric()){
                if (generMap != null) {
                    value = generMap.get(specParam.getId());
                }else {
                    log.error("通用参数获取失败，id值为 ",specParam.getId());
                }
                //判断是否是数值类型
                if (specParam.getNumeric()){
                    // 如果是数值的话，判断该数值落在那个区间
                    value = chooseSegment(value.toString(), specParam);
                }
            }else {
                if (specialMap != null) {
                    value = specialMap.get(specParam.getId());
                }else {
                    log.error("特定参数获取失败，id值为 ",specParam.getId());
                }
            }
            specMap.put(key,value);
        }));
        Goods goods = new Goods();
        goods.setBrandId(spu.getBrandId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setId(spu.getId());
        goods.setCreateTime(spu.getCreateTime());
        goods.setAll(searchText); //设置搜索条件   标题 /分类/品牌
        goods.setPrice(priceSet);  //价格集合
        goods.setSkus(JsonUtils.serialize(skus));  //sku集合的json数据
        goods.setSpecs(specMap); //规格参数
        goods.setSubTitle(spu.getSubTitle());
        return goods;
    }

    /**
     * 拼接查询字段
     * @param spu
     * @return
     */
    private String getSearchText(Spu spu){
        Brand brand = brandClient.getBrandById(spu.getBrandId());
        List<Category> categories = categoryClient.queryCategoryNamesByIds(
                Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        List<String> nameList = categories.stream().map(Category::getName).collect(Collectors.toList());
        String searchText = spu.getTitle() + StringUtils.join(nameList," ") + brand.getName();
        return searchText;
    }

    private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if(segs.length == 2){
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if(val >= begin && val < end){
                if(segs.length == 1){
                    result = segs[0] + p.getUnit() + "以上";
                }else if(begin == 0){
                    result = segs[1] + p.getUnit() + "以下";
                }else{
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }

    public PageResult<Goods> getGoodsList(SearchRequest searchRequest) {
        Integer page = searchRequest.getPage() -1;
        Integer size = searchRequest.getSize();
        String key = searchRequest.getKey();
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //1. 对key进行全文检索
        queryBuilder.withQuery(
                QueryBuilders.matchQuery("all",key)
                        .operator(Operator.AND));
        //2. 通过sourceFilter设置返回的结果字段,我们只需要id、skus、subTitle
        queryBuilder
                .withSourceFilter(new FetchSourceFilter(new String[]{"id","skus","subTitle"},null))
                //3. 分页
                .withPageable(PageRequest.of(page,size));

        /*//4. 查询获取结果  因为要做聚合，所以不能用Repository
        Page<Goods> goodsPage = goodsRepository.search(queryBuilder.build());
        int totalPages = goodsPage.getTotalPages();
        long total = goodsPage.getTotalElements();
        List<Goods> goodsList = goodsPage.getContent();*/
        //对查询条件进行聚合
        String categoryAggName = "category_agg";
        String brandAggName = "brand_agg";
        queryBuilder
                .addAggregation(AggregationBuilders.terms(categoryAggName).field("cid3"))
                .addAggregation(AggregationBuilders.terms(brandAggName).field("brandId"));

        AggregatedPage<Goods> result = elasticsearchTemplate.queryForPage(queryBuilder.build(), Goods.class);
        int totalPages = result.getTotalPages();
        long total = result.getTotalElements();
        //获取到聚合的数据
        Aggregations aggregations = result.getAggregations();
        List<Category> categoryList = parseCategoryList(aggregations.get(categoryAggName));
        List<Brand> brandList = parseBrandList(aggregations.get(brandAggName));
        //获取查询的数据
        List<Goods> goodsList = result.getContent();
        return new SearchResult(total,totalPages,goodsList,categoryList,brandList);
    }

    private List<Brand> parseBrandList(LongTerms terms) {
        List<Brand> brandList = null;
        try {
            List<Long> longList = terms.getBuckets()
                    .stream()
                    .map(bucket -> bucket.getKeyAsNumber().longValue())
                    .collect(Collectors.toList());
            brandList = brandClient.getBrandByIds(longList);
        } catch (Exception e) {
            log.error("聚合brand失败",e);
        }
        return brandList;
    }

    private List<Category> parseCategoryList(LongTerms terms) {
        List<Category> categoryList = null;
        try {
            List<Long> cidList = terms.getBuckets()
                    .stream()
                    .map(bucket -> bucket.getKeyAsNumber().longValue())
                    .collect(Collectors.toList());
            categoryList = categoryClient.queryCategoryNamesByIds(cidList);
        } catch (Exception e) {
            log.error("聚合category失败",e);
        }
        return categoryList;
    }
}
