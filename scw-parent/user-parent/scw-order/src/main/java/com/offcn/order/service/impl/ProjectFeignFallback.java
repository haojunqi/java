package com.offcn.order.service.impl;

import com.offcn.order.pojo.TReturn;
import com.offcn.order.service.ProjectFeignService;
import com.offcn.response.AppResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProjectFeignFallback implements ProjectFeignService {
    @Override
    public AppResponse<List<TReturn>> getReturns(Integer projectId) {
        AppResponse<List<TReturn>> fail = AppResponse.fail(null);
        fail.setMsg("远程调用超时");
        return fail ;
    }

    @Override
    public AppResponse<TReturn> getReturn(Integer returnId) {
        AppResponse<TReturn> fail = AppResponse.fail(null);
        fail.setMsg("远程调用超时");
        return fail;
    }
}
