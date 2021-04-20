package com.offcn.order.service.impl;

import com.offcn.order.enums.OrderStatusEnumes;
import com.offcn.order.mapper.TOrderMapper;
import com.offcn.order.pojo.TOrder;
import com.offcn.order.pojo.TReturn;
import com.offcn.order.reqvo.OrderInfoSubmitVo;
import com.offcn.order.service.OrderService;
import com.offcn.order.service.ProjectFeignService;
import com.offcn.order.utils.DateUtils;
import com.offcn.response.AppResponse;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Reference;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
@Service
public class OrderServiceImpl implements OrderService {
    @Autowired(required = false)
    private TOrderMapper orderMapper;
    @Autowired(required = false)
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ProjectFeignService projectFeignService;
    /*保存订单信息*/
    @Override
    public TOrder saveOrder(OrderInfoSubmitVo ordervo) {
        TOrder tOrder = new TOrder();
        String member = stringRedisTemplate.opsForValue().get(ordervo.getAccessToken());
        if (StringUtils.isEmpty(member)){
            throw new RuntimeException("用户没有登录");
        }
        tOrder.setMemberid(Integer.parseInt(member));
        BeanUtils.copyProperties(ordervo,tOrder);
        tOrder.setOrdernum(UUID.randomUUID().toString().replace("-",""));
        tOrder.setCreatedate(DateUtils.getFormatNow());
        tOrder.setStatus(OrderStatusEnumes.UNPAY.getCode());
        AppResponse<TReturn> response = projectFeignService.getReturn(ordervo.getReturnid());
        TReturn treturn = response.getData();
        System.out.println(treturn);
        tOrder.setMoney(treturn.getSupportmoney()*tOrder.getRtncount()+treturn.getFreight());
        orderMapper.insert(tOrder);
        return tOrder;
    }
}
