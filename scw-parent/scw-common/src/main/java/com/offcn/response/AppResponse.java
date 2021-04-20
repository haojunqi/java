package com.offcn.response;

import com.offcn.enums.ResponseCodeEnum;

import javax.annotation.Resource;

public class AppResponse<T> {
        private Integer code;
        private String msg;
        private  T data;  //泛型，跟不确定的类型


    /*相应成功的方法*/
    public static<T> AppResponse<T> ok(T data){
        AppResponse<T> response = new AppResponse<>();
        response.setCode(ResponseCodeEnum.SUCCESS.getCode());
        response.setMsg(ResponseCodeEnum.SUCCESS.getMessage());
        response.setData(data);
        return response;
    }
    /*响应失败的方法*/
    public static<T> AppResponse<T> fail(T data){
        AppResponse<T> response = new AppResponse<>();
        response.setCode(ResponseCodeEnum.FAIL.getCode());
        response.setMsg(ResponseCodeEnum.FAIL.getMessage());
        response.setData(data);
        return response;
    }
    public AppResponse() {
    }

    public AppResponse(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
