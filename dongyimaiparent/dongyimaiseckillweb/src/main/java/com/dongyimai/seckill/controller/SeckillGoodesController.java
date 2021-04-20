package com.dongyimai.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dongyimai.entity.Result;
import com.dongyimai.pojo.TbSeckillGoods;
import com.dongyimai.seckill.service.SeckillGoodesServic;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Security;
import java.util.List;

@RestController
@RequestMapping("/seckill")
public class SeckillGoodesController {
    @Reference
    private SeckillGoodesServic seckillGoodesServic;

    /*获取所有的秒杀商品*/
    @RequestMapping("/getAll")
    public List<TbSeckillGoods> getAll(){
        List<TbSeckillGoods> SeckillGoodes = seckillGoodesServic.findAllSeckillGoodes();
        return SeckillGoodes;
    }

    @RequestMapping("getOne")
    public TbSeckillGoods getOneGoods(Long id){
        return  seckillGoodesServic.findseckillgoodsById(id);
    }

    /*提交订单*/
    @RequestMapping("/submigOrder")
    public Result submitOrder(Long seckillGoodsId){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        if (name==null || name.isEmpty()){
            return new Result(false,"nologin");
        }
        try {
            seckillGoodesServic.submitOrder(seckillGoodsId,name);
            return new Result(true,"提交成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"提交失败");
        }
    }

}
