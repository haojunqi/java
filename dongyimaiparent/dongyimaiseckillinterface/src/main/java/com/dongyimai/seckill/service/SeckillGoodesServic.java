package com.dongyimai.seckill.service;

import com.dongyimai.pojo.TbSeckillGoods;
import com.dongyimai.pojo.TbSeckillOrder;

import java.util.List;

public interface SeckillGoodesServic {

    /*获取所有商品秒杀列表*/
    public List<TbSeckillGoods> findAllSeckillGoodes();

    /*根据商品的id获取详情页*/
    public TbSeckillGoods findseckillgoodsById(Long id);

    /*提交订单*/
    public void submitOrder(Long seckillGoodsId,String userId);


    /*根据用户名获取订单*/
    public TbSeckillOrder searchByUserId(String userId);

    /*支付订单*/
    public void saveOrder(String  userId,Long orderId ,String transactionId);

    /*支付失败订单删除，数量回归*/
    public void deleteOrder(String userId,Long orderId);
}
