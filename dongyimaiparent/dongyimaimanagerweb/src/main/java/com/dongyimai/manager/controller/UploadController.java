package com.dongyimai.manager.controller;


import com.dongyimai.entity.Result;
import com.offcn.utils.FastDFSClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class UploadController {
    @RequestMapping("/upload")
    public Result upload(MultipartFile file){
        // 获取文件上传时的名字
        String oldName = file.getOriginalFilename();
        System.out.println(oldName);
        try {
            // 获取文件扩展名
            // ab.c.txt
            String exeName = oldName.substring(oldName.lastIndexOf(".") + 1);
            System.out.println(exeName);
            // 创建一个 FastDFSClient 的对象
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:application/client.conf");
            System.out.println(fastDFSClient.toString());
            // group1/M00/00/00/wKi8kmBSwluALavgAABS3oX5UCY543.jpg
            String path = fastDFSClient.uploadFile(file.getBytes(), exeName);
            // http://192.168.188.146/group1/M00/00/00/wKi8kmBSwluALavgAABS3oX5UCY543.jpg
            String url = "http://192.168.188.147/" + path;
            return new Result(true,url);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"上传失败");
        }
    }
/*   public Result upload(MultipartFile file)  {
       *//*获取文件上传的名字*//*
       String oldName = file.getOriginalFilename();

       *//*获取文件扩展名*//*
       String exeName = oldName.substring(oldName.lastIndexOf(".") + 1);

       *//*实现上传*//*
       FastDFSClient fastDFSClient = null;
       try {
           fastDFSClient = new FastDFSClient("classpath:application/client.conf");

           String path = fastDFSClient.uploadFile(file.getBytes(),exeName);
           String url = "http://192.168.188.147/"+path;
           return new Result(true,url);
       } catch (Exception e) {
           e.printStackTrace();
           return new Result(false,"上传失败");
       }

   }*/

}
