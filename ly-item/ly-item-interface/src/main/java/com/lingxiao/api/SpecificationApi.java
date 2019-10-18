package com.lingxiao.api;

import com.lingxiao.pojo.SpecGroup;
import com.lingxiao.pojo.SpecParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface SpecificationApi {
    @GetMapping("/spec/params")
    List<SpecParam> getGroupParamByPid(
            @RequestParam(value = "gid",required = false) Long gid,
            @RequestParam(value = "cid",required = false) Long cid,
            @RequestParam(value = "searching",required = false) Boolean searching
    );

    @GetMapping("/spec/groups/{cid}")
    List<SpecGroup> getGroupByCid(@PathVariable("cid") Long cid);


    @GetMapping("/spec/group")
    List<SpecGroup> getGroupAndParmsByCid(@RequestParam("cid") Long cid);
}
