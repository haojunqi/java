package com.dongyimai.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dongyimai.entity.Result;
import com.dongyimai.pay.service.AliPayService;
import com.dongyimai.pojo.TbSeckillOrder;
import com.dongyimai.seckill.service.SeckillGoodesServic;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayController {
    @Reference
    private AliPayService payService;

    @Reference
    private SeckillGoodesServic seckillGoodesServic;
    @RequestMapping("/queryCode")
    public Map createQrcode(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        TbSeckillOrder seckillOrder = seckillGoodesServic.searchByUserId(name);
        if (seckillOrder != null){
        return payService.creatNative(seckillOrder.getId()+"",seckillOrder.getMoney().toString());

        }else{
            return new HashMap();
        }
    }


    @RequestMapping("/queryStatus")
    public Result queryStatus(String out_order_no){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        while(true) {
            Map map = payService.queryPayStatus(out_order_no);
            if (map == null) {
                return new Result(false, "支付失败");
            }

            if (map.get("trade_status") != null && map.get("trade_status").equals("TRADE_SUCCESS")) {
                /*调用支付成功的service*/
                seckillGoodesServic.saveOrder(userId,Long.valueOf(out_order_no),map.get("trade_no").toString());
                return new Result(true, "支付成功");
            }

            if (map.get("trade_status") != null &&  map.get("trade_status").equals("TRADE_CLOSED")) {
                /*支付超时就在redis中删除订单*/
                seckillGoodesServic.deleteOrder(userId,Long.valueOf(out_order_no));
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
