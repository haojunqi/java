package com.dongyimai.cart.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.dongyimai.entity.Result;
import com.dongyimai.pay.service.AliPayService;
import com.offcn.utils.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sun.java2d.pipe.ValidatePipe;

import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayController {

    @Reference
    private AliPayService aliPayService;

    @RequestMapping("/queryCode")
    public Map creadQrCode(){
        IdWorker idWorker = new IdWorker();
        return  aliPayService.creatNative(idWorker.nextId()+"","1");
    }
    @RequestMapping("/qureyStatus")
    public Result queryStatus(String out_order_no){
        while(true) {
            Map map = aliPayService.queryPayStatus(out_order_no);
            if (map == null) {
                return new Result(false, "支付失败");
            }

            if (map.get("trade_status") != null && map.get("trade_status").equals("TRADE_SUCCESS")) {
                return new Result(true, "支付成功");
            }

            if (map.get("trade_status") != null &&  map.get("trade_status").equals("TRADE_CLOSED")) {
                return new Result(false, "支付超时，请重新支付");
            }
            try {
                Thread.sleep(4000); // 4秒钟查询一次
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}

