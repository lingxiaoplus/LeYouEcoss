package com.lingxiao.web;

import com.lingxiao.pojo.SpecGroup;
import com.lingxiao.pojo.SpecParam;
import com.lingxiao.service.SpecificationService;
import com.lingxiao.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("specificationController")
@RequestMapping("/spec")
public class SpecificationController {

    @Autowired
    private SpecificationService specificationService;

    @GetMapping("/groups/{cid}")
    public ResponseEntity<List<SpecGroup>> getGroupByCid(@PathVariable("cid") Long cid){
        return ResponseEntity.ok(specificationService.getGroupByCid(cid));
    }

    @PostMapping("/group")
    public ResponseEntity<Void> addGroup(@RequestBody SpecGroup specGroup){
        specificationService.addGroup(specGroup);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/params")
    public ResponseEntity<List<SpecParam>> getGroupParamByPid(
            @RequestParam(value = "gid",required = false) Long gid,
            @RequestParam(value = "cid",required = false) Long cid,
            @RequestParam(value = "searching",required = false) Boolean searching
    ){
        return ResponseEntity.ok(specificationService.getGroupParamByGid(gid,cid,searching));
    }
}
