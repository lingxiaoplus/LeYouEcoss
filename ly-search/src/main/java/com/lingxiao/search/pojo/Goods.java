package com.lingxiao.search.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Document(indexName = "goods",shards = 1,type = "docs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Goods {
    @Id
    private Long id; //索引库id 实际上是spu的id
    @Field(type = FieldType.Text,analyzer = "ik_max_word")
    private String all; //所有需要被搜索的信息，包含标题，分类，品牌
    @Field(type = FieldType.Keyword, index = false)
    private String subTitle;// 卖点  副标题
    private Long brandId;// 品牌id
    private Long cid1;// 1级分类id
    private Long cid2;// 2级分类id
    private Long cid3;// 3级分类id
    private Date createTime;// 创建时间
    private Set<Long> price;// 价格
    @Field(type = FieldType.Keyword, index = false)
    private String skus;// List<sku>信息的json结构
    private Map<String, Object> specs;// 可搜索的规格参数，key是参数名，值是参数值

}
