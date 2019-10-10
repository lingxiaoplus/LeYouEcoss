package com.lingxiao.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/item")
public class GoodsDetailController {

    @GetMapping("/{id}.html")
    public String toItemPage(@PathVariable("id") Long id, Model model){

        return "item";
    }
}
