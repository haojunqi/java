package com.offcn.order.service;

import com.offcn.order.pojo.TOrder;
import com.offcn.order.reqvo.OrderInfoSubmitVo;
import org.springframework.cloud.openfeign.FeignClient;
public interface OrderService {

    public TOrder saveOrder(OrderInfoSubmitVo ordervo);
}
