package com.lingxiao.search.pojo;

import com.lingxiao.pojo.Brand;
import com.lingxiao.pojo.Category;
import com.lingxiao.vo.PageResult;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
public class SearchResult extends PageResult<Goods> {
    private List<Category> categories;
    private List<Brand> brands;
    private List<Map<String,Object>> filters;  //搜索过滤条件

    public SearchResult() {

    }
    public SearchResult(Long total, Integer totalPage, List<Goods> data, List<Category> categories, List<Brand> brands) {
        super(total, totalPage, data);
        this.categories = categories;
        this.brands = brands;
    }

    public SearchResult(Long total, Integer totalPage, List<Goods> data, List<Category> categories, List<Brand> brands, List<Map<String,Object>> filters) {
        super(total, totalPage, data);
        this.categories = categories;
        this.brands = brands;
        this.filters = filters;
    }
}
