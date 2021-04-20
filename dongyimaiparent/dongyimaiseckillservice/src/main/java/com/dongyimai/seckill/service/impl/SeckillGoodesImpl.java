package com.dongyimai.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.dongyimai.mapper.TbSeckillGoodsMapper;
import com.dongyimai.mapper.TbSeckillOrderMapper;
import com.dongyimai.pojo.TbSeckillGoods;
import com.dongyimai.pojo.TbSeckillGoodsExample;
import com.dongyimai.pojo.TbSeckillOrder;
import com.dongyimai.seckill.service.SeckillGoodesServic;
import com.offcn.utils.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Date;
import java.util.List;

@Service
public class SeckillGoodesImpl implements SeckillGoodesServic {

    @Autowired
    private TbSeckillGoodsMapper tbSeckillGoodsMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private TbSeckillOrderMapper seckillOrderMapper;
    /*获取所有的秒杀商品*/
    @Override
    public List<TbSeckillGoods> findAllSeckillGoodes() {
        /*从redis中获取数据*/
        List<TbSeckillGoods> list = redisTemplate.boundHashOps("seckillgoods").values();
        if (list == null || list.size() == 0) {
            TbSeckillGoodsExample example = new TbSeckillGoodsExample();
            TbSeckillGoodsExample.Criteria criteria = example.createCriteria();
            criteria.andStatusEqualTo("1");
            criteria.andNumGreaterThan(0);
            criteria.andEndTimeGreaterThan(new Date());
            list = tbSeckillGoodsMapper.selectByExample(example);
            /*写入redis*/
            for (TbSeckillGoods goods : list) {
                redisTemplate.boundHashOps("seckillgoods").put(goods.getId(), goods);
            }
        }
        return list;
    }

    @Override
    public TbSeckillGoods findseckillgoodsById(Long id) {
        /*在redis中获取具体一个商品的详细信息*/
        return (TbSeckillGoods) redisTemplate.boundHashOps("seckillgoods").get(id);

    }

    @Override
    public void submitOrder(Long seckillGoodsId, String userId) {
        /*判断秒杀是否可以正常进行*/
        TbSeckillGoods seckillgoods = (TbSeckillGoods) redisTemplate.boundHashOps("seckillgoods").get(seckillGoodsId);
        if (seckillgoods == null) {
            throw new RuntimeException("秒杀结束");
        }
        if (seckillgoods.getStockCount() <= 0) {
            throw new RuntimeException("来晚了，商品已售空");
        }
        if (seckillgoods.getStartTime().getTime() >= new Date().getTime()) {
            throw new RuntimeException("秒杀结束");
        }
        if (seckillgoods.getEndTime().getTime() <= new Date().getTime()) {
            throw new RuntimeException("秒杀结束");
        }
        /*抢购开始，提交订单*/
        /*修改商品剩余数量*/
        seckillgoods.setStockCount(seckillgoods.getStockCount() - 1);
        redisTemplate.boundHashOps("seckillgoods").put(seckillGoodsId, seckillgoods);
        /*添加订单信息*/
        TbSeckillOrder seckillOrder = new TbSeckillOrder();
        seckillOrder.setId(new IdWorker().nextId());
        seckillOrder.setSeckillId(seckillGoodsId);
        seckillOrder.setCreateTime(new Date());
        seckillOrder.setMoney(seckillgoods.getCostPrice());
        seckillOrder.setSellerId(seckillgoods.getSellerId());
        seckillOrder.setUserId(userId);
        seckillOrder.setStatus("0");
        redisTemplate.boundHashOps("seckillOrder").put(userId, seckillOrder);
    }

    @Override
    public TbSeckillOrder searchByUserId(String userId) {
        TbSeckillOrder seckillOrder = (TbSeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);
        return seckillOrder;
    }

    @Override
    public void saveOrder(String userId, Long orderId, String transactionId) {
        TbSeckillOrder seckillOrder = (TbSeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);
        if (seckillOrder ==null){
            throw new RuntimeException("订单不存在");
        }
        /**/
        if (orderId.equals(seckillOrder.getId())){
            throw new RuntimeException("订单错误");
        }
        seckillOrder.setTransactionId(transactionId);
        seckillOrder.setPayTime(new Date());
        seckillOrder.setStatus("1");
        /*写入到数据库*/
         seckillOrderMapper.insert(seckillOrder);
         redisTemplate.boundHashOps("s").delete(userId);

    }

    @Override
    public void deleteOrder(String userId, Long orderId) {
        /*根据用户id查询订单*/
        TbSeckillOrder seclillOrder = (TbSeckillOrder) redisTemplate.boundHashOps("seclillOrder").get(userId);
        redisTemplate.boundHashOps("seckillOrder").delete(userId);
        /*在缓存中删除的订单，原来的库存数回归*/
        TbSeckillGoods seckillgoods = (TbSeckillGoods)redisTemplate.boundHashOps("seckillgoods").get(seclillOrder.getSeckillId());
        if (seckillgoods!=null){
            seckillgoods.setStockCount(seckillgoods.getStockCount()+1);
            redisTemplate.boundHashOps("seckillgoods").put(seckillgoods.getId(),seckillgoods);
        }

    }
}
