package com.offcn.project.service.impl;

import com.alibaba.druid.support.json.JSONUtils;
import com.offcn.project.enums.ProjectImageTypeEnume;
import com.offcn.project.mapper.*;
import com.offcn.project.pojo.*;
import com.offcn.project.service.ProjectCreatservice;
import com.offcn.project.vo.req.ProjectToReidsVo;
import io.swagger.models.auth.In;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
@Service
public class ProjectCreatServiceImpl implements ProjectCreatservice {
    @Autowired(required = false)
    private TProjectMapper tProjectMapper;
    @Autowired(required = false)
    private TProjectImagesMapper imagesMapper;
    @Autowired(required = false)
    private TProjectTypeMapper typeMapper;
    @Autowired(required = false)
    private TReturnMapper tReturnMapper;
    @Autowired(required = false)
    private TProjectTagMapper tagMapper;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Override
    public String creatProject(Integer memberId) {

        String token = UUID.randomUUID().toString().replace("-", "");
        ProjectToReidsVo projectToReidsVo = new ProjectToReidsVo();
        projectToReidsVo.setMemberid(memberId);
        projectToReidsVo.setAccessToken(token);
        String jsonString = JSONUtils.toJSONString(projectToReidsVo);
        redisTemplate.opsForValue().set(token,jsonString);
        return token;
    }

    @Override
    public void saveProjectInfo(ProjectToReidsVo reidsVo) {
        TProject tProject = new TProject();
        BeanUtils.copyProperties(reidsVo,tProject);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd: HH:mm:ss");
        tProject.setCreatedate(simpleDateFormat.format(new Date()));
        //插入数据库
        tProjectMapper.insert(tProject);
        //图片的插入
        Integer id = tProject.getId();
        TProjectImages headerImages = new TProjectImages(id,reidsVo.getHeaderImage(), ProjectImageTypeEnume.HEADER.getCode());
        imagesMapper.insert(headerImages);
        /*详细图片的插入*/
        List<String> detailsImages = reidsVo.getDetailsImages();
        for (String datail :detailsImages){
            TProjectImages tProjectImages = new TProjectImages(id,datail,ProjectImageTypeEnume.DETAILS.getCode());
            imagesMapper.insert(tProjectImages);
        }

        List<Integer> tagIds = reidsVo.getTagIds();
        for (Integer id_t: tagIds){
            TProjectTag tProjectTag = new TProjectTag(id, id_t);
            tagMapper.insert(tProjectTag);
        }
        /*类型*/

        List<Integer> typeIds = reidsVo.getTypeIds();
        for (Integer type : typeIds){
            TProjectType tProjectType = new TProjectType(id, type);
            typeMapper.insert(tProjectType);
        }
        List<TReturn> returns = reidsVo.getProjectReturns();
        for (TReturn treturn:returns){
            treturn.setProjectid(id);
            tReturnMapper.insert(treturn);
        }

        /*删除redis中的数据*/
       // redisTemplate.delete(reidsVo.getProjectToken());
    }
}
