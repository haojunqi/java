package com.offcn.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

public class HttpUtilsTest {
    public static void main(String[] args) throws IOException {
        DefaultHttpClient client = new DefaultHttpClient();//创建http客户端
        //2、构建请求，通过客户端，发送请求

        HttpGet request = new HttpGet("http://www.ujiuye.com");
        //发送请求
        HttpResponse response = client.execute(request);

        HttpEntity entity = response.getEntity();

    }
}
