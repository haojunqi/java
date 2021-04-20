package com.offcn.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class OssTemplate {

// yourEndpoint填写Bucket所在地域对应的Endpoint。以华东1（杭州）为例，Endpoint填写为https://oss-cn-hangzhou.aliyuncs.com。
    private String endpoint = "oss-cn-beijing.aliyuncs.com";
   // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
    private String accessKeyId = "LTAI5tPz9w7sN7BgUQQaKdey";
    private String accessKeySecret = "gvNaxQPg46t1MhZu4oxvi1qdibG6ny";
    private String bucketName="junqi-offcn";
    private String bucketurl="https://junqi-offcn.oss-cn-beijing.aliyuncs.com";

    /*文件上传*/
    public String uoloda(InputStream is, String fileName)  {
        System.out.println(fileName);
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        // 填写本地文件的完整路径。如果未指定本地路径，则默认从示例程序所属项目对应本地路径中上传文件流。
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String format = simpleDateFormat.format(new Date());
        /*创建文件名*/
        String newfileName = UUID.randomUUID().toString().replace("-", "")+fileName.substring(fileName.lastIndexOf("."));

// 填写Bucket名称和Object完整路径。Object完整路径中不能包含Bucket名称。
        ossClient.putObject(bucketName, "imag/"+format+"/"+newfileName, is);
        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
// 关闭OSSClient。
        ossClient.shutdown();
        return bucketurl+"/img/"+format+"/"+newfileName;
    }
}
