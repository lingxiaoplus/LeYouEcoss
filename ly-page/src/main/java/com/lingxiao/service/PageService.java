package com.lingxiao.service;

import com.lingxiao.client.BrandClient;
import com.lingxiao.client.CategoryClient;
import com.lingxiao.client.GoodsClient;
import com.lingxiao.client.SpecificationClient;
import com.lingxiao.pojo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class PageService {
    @Autowired
    private BrandClient brandClient;
    @Autowired
    private CategoryClient categoryClient;
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private SpecificationClient specClient;

    @Autowired
    private TemplateEngine templateEngine;

    public Map<String, Object> loadModel(Long id) {
        Map<String,Object> map = new HashMap<>();
        Spu spu = goodsClient.getSpuById(id);
        List<Sku> skus = goodsClient.getSkusByPid(id);
        SpuDetail spuDetail = goodsClient.getGoodsDetail(id);

        Brand brand = brandClient.getBrandById(spu.getBrandId());
        List<Category> categories = categoryClient.queryCategoryNamesByIds(
                Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        List<SpecGroup> specs = specClient.getGroupByCid(spu.getCid3());

        map.put("title",spu.getTitle());
        map.put("subTitle",spu.getSubTitle());
        map.put("skus",skus);
        map.put("spuDetail",spuDetail);
        map.put("brand",brand);
        map.put("categories",categories);
        map.put("specs",specs);
        return map;
    }

    public void createHtml(Long id){
        //thymeleaf的上下文  用于管理数据
        Context context = new Context();
        context.setVariables(loadModel(id));
        File file = new File("D:\\static\\item", id + ".html");
        if (file.exists()){
            file.delete();
        }
        try (PrintWriter writer = new PrintWriter(file,"UTF-8")){
            //生成html
            templateEngine.process("item",context,writer);
        }catch (IOException ex){
            log.error("[静态页服务 生成静态页异常！]",ex);
        }
    }

    public void deleteHtml(Long spuId) {
        File file = new File("D:\\static\\item", spuId + ".html");
        if (file.exists()){
            file.delete();
        }
    }
}
