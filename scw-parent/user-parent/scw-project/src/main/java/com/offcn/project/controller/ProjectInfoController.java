package com.offcn.project.controller;

import com.offcn.project.pojo.*;
import com.offcn.project.service.ProjectInfoService;
import com.offcn.project.vo.resp.ProjectDetailVo;
import com.offcn.project.vo.resp.ProjectVo;
import com.offcn.response.AppResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Api(tags = "项目的详细信息")
@RequestMapping("/projectInfo")
@RestController
public class ProjectInfoController {
    @Autowired
    private ProjectInfoService projectInfoService;

    @ApiOperation(value = "根据项目id获取返回信息")
    @PostMapping("/getReturns/{projectId}")
    public AppResponse<List<TReturn>> getReturns(@PathVariable("projectId") Integer projectId){
        System.out.println(projectId);
        return AppResponse.ok(projectInfoService.getReturnList(projectId));
    }


    @ApiOperation(value = "根据回报id回去返回信息")
    @PostMapping ("/getReturn/{returnId}")
    public AppResponse<TReturn> getReturn(@PathVariable("returnId")Integer returnId){
        System.out.println("根据回报id获取"+returnId);
        return AppResponse.ok(projectInfoService.getReturn(returnId));
    }

    @ApiOperation(value = "获取所有的项目")
    @GetMapping("/getAllPorject")
    public AppResponse<List<ProjectVo>> getAllProject(){
        List<TProject> allProject = projectInfoService.getAllProject();
        ArrayList<ProjectVo> pvos = new ArrayList<>();
        for (TProject pro:allProject){
            ProjectVo vo = new ProjectVo();
            BeanUtils.copyProperties(pro,vo);
            List<TProjectImages> projectImages = projectInfoService.getProjectImage(pro.getId());
            for (TProjectImages image: projectImages) {
                    if (image.getImgtype()==0){
                        vo.setHeaderImage(image.getImgurl());
                    }
            }
            pvos.add(vo);
        }
        return AppResponse.ok(pvos);
    }

    /*获取项目的详细信息*/
    @ApiOperation(value = "获取项目的详细信息")
    @GetMapping("/getOne/{projectId}")
    public AppResponse<ProjectDetailVo> getOne(@PathVariable("projectId") Integer projectId){
        ProjectDetailVo projectDetailVo = new ProjectDetailVo();
        TProject one = projectInfoService.getOne(projectId);
        BeanUtils.copyProperties(one,projectDetailVo);
        /*添加图片*/
        List<TProjectImages> projectImages = projectInfoService.getProjectImage(projectId);
        List detailsImage = new ArrayList<>();
        if (projectImages==null||projectImages.size()==0){
            projectImages=new ArrayList<>();
        }
        for (TProjectImages image : projectImages) {
            if (image.getImgtype()==0){
                /*等于0就是头图片*/
                projectDetailVo.setHeaderImage(image.getImgurl());
            }else {
                    detailsImage.add(projectDetailVo.getDetailsImage());
            }
        }

            projectDetailVo.setDetailsImage(detailsImage);
        /*回报信息*/
        List<TReturn> returnList = projectInfoService.getReturnList(projectId);
        projectDetailVo.setProjectReturns(returnList);
        return AppResponse.ok(projectDetailVo);
    }

    /*获取所有的标签*/
    @ApiOperation(value = "获取标签")
    @GetMapping("/gettag")
    public AppResponse<List<TTag>> findAllTag(){
        return AppResponse.ok(projectInfoService.findAllTag());
    }

    @ApiOperation(value = "获取分类")
    @GetMapping("/gettype")
    public AppResponse<List<TType>> findAllType(){
        return AppResponse.ok(projectInfoService.findAllType());
    }
}
