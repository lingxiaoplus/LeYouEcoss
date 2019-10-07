package com.lingxiao.search.repository;

import com.lingxiao.pojo.Spu;
import com.lingxiao.search.client.GoodsClient;
import com.lingxiao.search.pojo.Goods;
import com.lingxiao.search.service.SearchService;
import com.lingxiao.vo.PageResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
@RunWith(SpringRunner.class)
@SpringBootTest
public class GoodsRepositoryTest {
    @Autowired
    private ElasticsearchTemplate template;
    @Autowired
    private ElasticsearchRepository repository;
    @Autowired
    private SearchService searchService;
    @Autowired
    private GoodsClient goodsClient;

    @Test
    public void createIndex(){
        template.createIndex(Goods.class);
        template.putMapping(Goods.class);
    }

    /**
     * 导入数据
     */
    @Test
    public void inputGoods(){
        int page = 1;
        int rowCount =100;
        int size = 0;
        do {
            PageResult<Spu> result = goodsClient.getGoodsByPage(page, rowCount, null, null);
            List<Spu> spuList = result.getItems();
            if (CollectionUtils.isEmpty(spuList)){
                break;
            }
            List<Goods> goodsList = spuList.stream().filter((spu)->
                searchService.createGoodsData(spu) != null
            ).map(
                    searchService::createGoodsData
            ).collect(Collectors.toList());
            repository.saveAll(goodsList);
            page++;
            size = spuList.size();
        }while (size == 100);
    }
}