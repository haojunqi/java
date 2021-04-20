package com.dongyimai.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.dongyimai.pay.service.AliPayService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
@Service(timeout = 6000000)
public class AliPayServiceImpl implements AliPayService {
    @Autowired
    private  AlipayClient alipayClient;


    @Override
    public Map creatNative(String out_trade_no, String total_amount) {
        Map map = new HashMap();
        AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest (); //创建API对应的request类
        request . setBizContent ( "{"   +
                "\"out_trade_no\":\"" + out_trade_no +  "\"," + //商户订单号
                "\"total_amount\":\"" + total_amount + "\","   +
                "\"subject\":\"Iphone6 16G\","   +
                "\"store_id\":\"NJ_001\","   +
                "\"timeout_express\":\"90m\"}" ); //订单允许的最晚付款时间
        try {
            AlipayTradePrecreateResponse response = alipayClient.execute (request);
            System.out.println("返回结果:" + response.getBody());
            if(response.getCode().equals("10000")){ // 调用正常
                map.put("qrcode",response.getQrCode());
                // 将来进行订单查询状态（查询支付状态）
                map.put("out_trade_no",out_trade_no);
                // 前端展示支付金额
                map.put("total_amount",total_amount);

            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return map;
    }

    @Override
    public Map queryPayStatus(String out_trade_no) {
        Map map = new HashMap();
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();//创建API对应的request类
        request.setBizContent("{" +
                "\"out_trade_no\":\"" + out_trade_no + "\"}"); //设置业务参数
        AlipayTradeQueryResponse response = null;//通过alipayClient调用API，获得对应的response类
        try {
            response = alipayClient.execute(request);
            // 交易的订单编号
            map.put("out_trade_no",out_trade_no);
            // 交易状态
            map.put("trade_status",response.getTradeStatus());
            map.put("trade_no",response.getTradeNo());
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        System.out.print(response.getBody());
        return map;
    }
}

