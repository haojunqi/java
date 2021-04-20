package com.offcn.project.service.impl;


import com.offcn.project.mapper.*;
import com.offcn.project.pojo.*;
import com.offcn.project.service.ProjectInfoService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectInfoServiceImpl implements ProjectInfoService {

    @Autowired(required = false)
    private TReturnMapper tReturnMapper;

    @Autowired
    private TProjectMapper tProjectMapper;

    @Autowired
    private TProjectImagesMapper tProjectImagesMapper;

    @Autowired
    private TTagMapper tagMapper;
    @Autowired
    private TTypeMapper typeMapper;
    @Override
    public List<TReturn> getReturnList(Integer projectId) {
        TReturnExample tReturnExample = new TReturnExample();
        TReturnExample.Criteria criteria = tReturnExample.createCriteria();
        criteria.andProjectidEqualTo(projectId);
        List<TReturn> tReturns = tReturnMapper.selectByExample(tReturnExample);
        System.out.println(tReturns.size());
        return  tReturns;
    }

    @Override
    public TReturn getReturn(Integer returnId) {
        return tReturnMapper.selectByPrimaryKey(returnId);
    }

/*获取所有的项目*/

    @Override
    public List<TProject> getAllProject() {
        return tProjectMapper.selectByExample(null);
    }

    @Override
    public List<TProjectImages> getProjectImage(Integer projectId) {
        TProjectImagesExample tProjectImagesExample = new TProjectImagesExample();
        TProjectImagesExample.Criteria criteria = tProjectImagesExample.createCriteria();
        criteria.andProjectidEqualTo(projectId);
        List<TProjectImages> tProjectImages = tProjectImagesMapper.selectByExample(tProjectImagesExample);
        return tProjectImages;
    }

    @Override
    public TProject getOne(Integer projectId) {
        return tProjectMapper.selectByPrimaryKey(projectId);
    }

    @Override
    public List<TTag> findAllTag() {
        return tagMapper.selectByExample(null);
    }

    /*获取所有的分类*/

    @Override
    public List<TType> findAllType() {
        return typeMapper.selectByExample(null);
    }
}
