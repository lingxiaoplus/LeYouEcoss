package com.lingxiao.search.pojo;

import com.lingxiao.pojo.Brand;
import com.lingxiao.pojo.Category;
import com.lingxiao.vo.PageResult;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
public class SearchResult extends PageResult<Goods> {
    private List<Category> categories;
    private List<Brand> brands;

    public SearchResult() {

    }
    public SearchResult(Long total, Integer totalPage, List<Goods> data, List<Category> categories, List<Brand> brands) {
        super(total, totalPage, data);
        this.categories = categories;
        this.brands = brands;
    }
}
