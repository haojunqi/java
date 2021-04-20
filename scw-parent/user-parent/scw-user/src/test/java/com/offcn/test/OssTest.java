package com.offcn.test;


import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.offcn.utils.OssTemplate;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class OssTest {
    public static void main(String[] args) throws FileNotFoundException {
        OssTemplate ossTemplate = new OssTemplate();
        InputStream inputStream = new FileInputStream("D:\\uplodaFile\\main.jpg");
        String uoloda = ossTemplate.uoloda(inputStream, "main.jpg");
        System.out.println(uoloda);
    }
}
