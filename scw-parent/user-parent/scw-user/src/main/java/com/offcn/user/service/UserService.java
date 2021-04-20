package com.offcn.user.service;

import com.offcn.user.pojo.TMember;
import com.offcn.user.pojo.TMemberAddress;
import com.offcn.user.vo.response.UserRspeonse;
import io.swagger.models.auth.In;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;

public interface UserService {
    public void registerUser(TMember member);


    /*用户登录*/
    public TMember login(String loginacct,String userpswd);

    /*根据用户的id获取一个用户*/
    public TMember findById(Integer id);

    /*获取收件地址*/
    public List<TMemberAddress> getAddress(Integer memberId);
}
