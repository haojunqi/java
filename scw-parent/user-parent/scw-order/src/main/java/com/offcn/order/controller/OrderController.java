package com.offcn.order.controller;

import com.offcn.order.pojo.TOrder;
import com.offcn.order.reqvo.OrderInfoSubmitVo;
import com.offcn.order.service.OrderService;
import com.offcn.response.AppResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "保存订单")
@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired(required = false)
    private OrderService orderService;

    @ApiOperation(value = "添加订单")
    @PostMapping("/creatOrder")
    public AppResponse<TOrder> creatOrder(OrderInfoSubmitVo vo){
        TOrder tOrder = orderService.saveOrder(vo);
        return AppResponse.ok(tOrder);
    }

}
