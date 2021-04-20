package com.offcn.project.service;

import com.offcn.project.pojo.*;
import io.swagger.models.auth.In;

import java.util.List;

public interface ProjectInfoService {

    /*根据项目ip获取返回信息*/
    public List<TReturn> getReturnList(Integer projectId);

    /*根据返回信息的id获取返回的对象*/
    public TReturn getReturn(Integer returnId);

    /*获取所有的项目*/
    public List<TProject> getAllProject();

    /*获取头图片信息*/
    public List<TProjectImages> getProjectImage(Integer projectId);

    /*获取项目的详细信息*/
    public TProject getOne(Integer projectId);

    /*获取所有的标签*/
    public List<TTag> findAllTag();

    /*获取所有的分类*/
    public List<TType> findAllType();
}
