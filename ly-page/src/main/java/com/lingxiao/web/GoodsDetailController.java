package com.lingxiao.web;

import com.lingxiao.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/item")
public class GoodsDetailController {

    @Autowired
    private PageService pageService;
    @GetMapping("/{id}.html")
    public String toItemPage(@PathVariable("id") Long id, Model model){
        //准备模型数据
        Map<String,Object> attributes = pageService.loadModel(id);
        model.addAllAttributes(attributes);
        return "item";
    }
}
