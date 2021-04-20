package com.offcn.user.component;


import com.offcn.utils.HttpUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class SmsTemplate {
    @Value("${sms.host}")
    private String host;
    @Value("${sms.path}")
    private String path;
    @Value("${sms.method}")
    private String method;
    @Value("${sms.appcode}")
    private String appcode;


    public String sendSma(String mobile,String code){
        HashMap headers = new HashMap<>();
        headers.put("Authorization","APPCODE"+code);

        HashMap query = new HashMap<>();
        query.put("mobile",mobile);
        query.put("param","code"+code);
        query.put("tpl_id","TP1711063");
        HttpResponse response=null;
        try {
            response = HttpUtils.doPost(host, path, method, headers, query, "");
            return EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }


    }
}
