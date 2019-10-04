package com.lingxiao.vo;

import lombok.Data;

import java.util.List;

@Data
public class PageResult<T> {
    private Long total;  //总共有多少条数据
    private Integer totalPage;  //一共有多少页
    private List<T> data;


    public PageResult(Long total, List<T> data) {
        this.total = total;
        this.data = data;
    }

    public PageResult(Long total, Integer totalPage, List<T> data) {
        this.total = total;
        this.totalPage = totalPage;
        this.data = data;
    }
}
