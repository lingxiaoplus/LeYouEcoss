package com.lingxiao.service;

import com.lingxiao.enums.ExceptionEnum;
import com.lingxiao.exception.LyException;
import com.lingxiao.mapper.SpecGroupMapper;
import com.lingxiao.mapper.SpecGroupParamMapper;
import com.lingxiao.pojo.SpecGroup;
import com.lingxiao.pojo.SpecParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service("specificationService")
public class SpecificationService {
    @Autowired
    private SpecGroupMapper specGroupMapper;
    @Autowired
    private SpecGroupParamMapper paramMapper;

    public List<SpecGroup> getGroupByCid(Long cid) {
        SpecGroup specGroup = new SpecGroup();
        specGroup.setCid(cid);
        List<SpecGroup> specGroups = specGroupMapper.select(specGroup);
        if (CollectionUtils.isEmpty(specGroups)){
            throw new LyException(ExceptionEnum.SELECT_GROUP_NOT_FOUNT);
        }
        return specGroups;
    }

    public void addGroup(SpecGroup specGroup) {

        if (specGroup == null){
            throw new LyException(ExceptionEnum.ILLEGA_ARGUMENT);
        }
        System.out.println("传递过来的参数  "+specGroup);
        int rowCount = specGroupMapper.insert(specGroup);
        if (rowCount != 1){
            throw new LyException(ExceptionEnum.GROUP_SAVE_ERROR);
        }
    }

    public List<SpecParam> getGroupParamByGid(Long gid,Long cid,Boolean searching) {
        SpecParam specParam = new SpecParam();
        specParam.setGroupId(gid);
        specParam.setCid(cid);
        specParam.setSearching(searching);
        List<SpecParam> list = paramMapper.select(specParam);
        if (CollectionUtils.isEmpty(list)){
            // TODO: 2019/10/7 暂时去除  不然在添加elasticsearch索引库的时候会因为一些脏数据添加失败
            //throw new LyException(ExceptionEnum.SELECT_GROUP_PARAM_NOT_FOUNT);
        }
        return list;
    }
}
