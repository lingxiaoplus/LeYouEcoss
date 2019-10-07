package com.lingxiao.search.client;

import com.lingxiao.pojo.Category;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
@RunWith(SpringRunner.class)
@SpringBootTest
public class CategoryClientTest {
    @Autowired
    private CategoryClient categoryClient;

    @Test
    public void getCategory(){
        List<Category> categories = categoryClient.queryCategoryNamesByIds(Arrays.asList(1L, 2L, 3L));
        categories.forEach(category -> {
            System.out.println("category = " + category);
        });
    }
}