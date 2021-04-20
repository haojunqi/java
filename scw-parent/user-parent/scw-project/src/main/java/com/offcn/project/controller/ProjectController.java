package com.offcn.project.controller;


import com.offcn.project.utils.OssTemplate;
import com.offcn.response.AppResponse;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
@Api(tags = "项目模块，完成文件的上传")
@RequestMapping("/project")
public class ProjectController {
    /*调用工具类*/
    @Autowired
    private OssTemplate ossTemplate;
    @PostMapping("/upload")
    public AppResponse<Map<String,Object>> upload(@RequestParam("file")MultipartFile[] files) throws IOException {
        /*用来作为返回的参数*/
        HashMap<String, Object> map = new HashMap<>();
        /*用来存放多个对象文件*/
        ArrayList<Object> filesurl = new ArrayList<>();
        for (MultipartFile file:files){
            String fileurl = ossTemplate.uoloda(file.getInputStream(), file.getOriginalFilename());
            filesurl.add(fileurl);
        }
        map.put("urls",filesurl);
        return AppResponse.ok(map);
    }
}
