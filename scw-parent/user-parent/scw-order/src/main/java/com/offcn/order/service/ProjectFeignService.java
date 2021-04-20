package com.offcn.order.service;

import com.offcn.order.pojo.TReturn;
import com.offcn.order.service.impl.ProjectFeignFallback;
import com.offcn.response.AppResponse;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient(value = "scw-project",fallback = ProjectFeignFallback.class) //远程调用的方法（注册到注册中心的服务名）
public interface ProjectFeignService {


    @PostMapping("/projectInfo/getReturns/{projectId}")
    public AppResponse<List<TReturn>> getReturns(@PathVariable("projectId") Integer projectId);

    @PostMapping ("/projectInfo/getReturn/{returnId}")
    public AppResponse<TReturn> getReturn(@PathVariable("returnId")Integer returnId);
}
