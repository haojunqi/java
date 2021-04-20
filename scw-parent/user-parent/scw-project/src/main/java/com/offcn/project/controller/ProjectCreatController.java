package com.offcn.project.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.offcn.project.pojo.TReturn;
import com.offcn.project.service.ProjectCreatservice;
import com.offcn.project.vo.req.ProjectBaseInfoVo;
import com.offcn.project.vo.req.ProjectToReidsVo;
import com.offcn.project.vo.req.ReturnInfoVo;
import com.offcn.response.AppResponse;
import com.offcn.vo.BaseVo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@Api(tags = "项目的创建")
@RequestMapping("/creat")
public class ProjectCreatController {

    @Autowired
    private ProjectCreatservice projectCreatservice;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @ApiOperation(value = "项目的初始化")
    @PostMapping("/init")
    public AppResponse<String> init(BaseVo baseVo){
        //获取发起者的id
        String token = baseVo.getAccessToken();
        String memberId = redisTemplate.opsForValue().get(token);
        if (StringUtils.isEmpty(memberId)){
            return AppResponse.fail("用户没有登录，登录");
        }
        int newmemberId = Integer.parseInt(memberId);
        String projecttoken = projectCreatservice.creatProject(newmemberId);
        return AppResponse.ok(projecttoken);
    }


    @ApiOperation(value = "添加项目的基本信息")
    @PostMapping("/addBaseInfo")
    public AppResponse<String> addBaseInfo(ProjectBaseInfoVo baseInfoVo){
        String accesstoken = redisTemplate.opsForValue().get(baseInfoVo.getAccesstoken());
        if (StringUtils.isEmpty(accesstoken)){
            return AppResponse.fail("用户没有登录");
        }
        //从redis中获取数据
        String projecttoken = redisTemplate.opsForValue().get(baseInfoVo.getProjectToken());
        ProjectToReidsVo projectToReidsVo = JSON.parseObject(projecttoken, ProjectToReidsVo.class);
        /*添加基本信息*/
        BeanUtils.copyProperties(baseInfoVo,projectToReidsVo);
        //项目写入redis
        String jsonString = JSON.toJSONString(projectToReidsVo);
        redisTemplate.opsForValue().set(baseInfoVo.getProjectToken(),jsonString);
        return AppResponse.ok(baseInfoVo.getProjectToken());
    }


    @ApiOperation(value = "添加汇报信息")
    @PostMapping("/addreturninfo")
    public AppResponse<String> addreturnInfo(@RequestBody List<ReturnInfoVo> returns){
        /*判断用户是否登录*/
        String memberId = redisTemplate.opsForValue().get(returns.get(0).getAccessToken());
        if (StringUtils.isEmpty(memberId)){
            return AppResponse.fail("用户没有登录");
        }
        String projectToken = returns.get(0).getProjectToken();
        ProjectToReidsVo projectToReidsVo = JSON.parseObject(projectToken, ProjectToReidsVo.class);
        ArrayList<TReturn> arrayList = new ArrayList();
        for (ReturnInfoVo infoVo : returns){
            TReturn tReturn = new TReturn();
            BeanUtils.copyProperties(infoVo,tReturn);
            arrayList.add(tReturn);
        }
        projectToReidsVo.setProjectReturns(arrayList);
        String jsonString = JSON.toJSONString(projectToReidsVo);
        redisTemplate.opsForValue().set(projectToken,jsonString);
        return AppResponse.ok(projectToken);
    }


    //数据添加到数据库
    @ApiOperation(value = "添加数据到数据库")
    @PostMapping("/savrproject")
    public AppResponse<String> saveProjectInfo(String accesstoken,String projectToken){
        String memberId = redisTemplate.opsForValue().get(accesstoken);
        if (StringUtils.isEmpty(memberId)){
            return AppResponse.fail("用户没有登录");
        }
        /*获取redis中的project*/
        String redisstr = redisTemplate.opsForValue().get(projectToken);
        ProjectToReidsVo redisvo = JSON.parseObject(redisstr,ProjectToReidsVo.class);
        projectCreatservice.saveProjectInfo(redisvo);
        return AppResponse.ok("成功");
    }
}
