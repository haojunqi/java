package com.offcn.user.controller;

import com.offcn.response.AppResponse;
import com.offcn.user.component.SmsTemplate;
import com.offcn.user.except.UserExisException;
import com.offcn.user.pojo.TMember;
import com.offcn.user.pojo.TMemberAddress;
import com.offcn.user.service.UserService;
import com.offcn.user.vo.UserRegisterVo;
import com.offcn.user.vo.response.UserRspeonse;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
public class UserController {
    @Autowired
    private SmsTemplate smsTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private UserService userService;


    @ApiOperation("获取验证码")   //说明方法的用途
    @PostMapping("/sendSms")
    public AppResponse<Object> sendSms(String mobile){
        //创建随机验证码
        String substring = UUID.randomUUID().toString().substring(0, 4);
        //根据用户的手机号为键存储验证码，有效期为五分钟
        stringRedisTemplate.opsForValue().set(mobile,substring,5, TimeUnit.MINUTES);
        //开始发送短信
        String sendReponse = smsTemplate.sendSma(mobile, substring);
        if (sendReponse==null || sendReponse.equals("fail")){
            return AppResponse.fail("验证码发送失败");
        }
        return AppResponse.ok(sendReponse);

    }

    @PostMapping("/register")
    public AppResponse<Object> register(UserRegisterVo vo){
        String code = stringRedisTemplate.opsForValue().get(vo.getLoginacct());
        if (!StringUtils.isEmpty(code)){
            /*比较是否相同*/
            if (!code.equals(vo.getCode())){
                return AppResponse.fail("验证码错误");
            }
        TMember tMember = new TMember();
        BeanUtils.copyProperties(vo,tMember);
            try {
                userService.registerUser(tMember);
                return AppResponse.ok("注册成功");
            } catch (Exception e) {
                e.printStackTrace();
                throw new UserExisException();
            }
        }else {
            return AppResponse.fail("验证码失效");
        }

    }

    @PostMapping("/login")
    public AppResponse<UserRspeonse> login(String loginacct,String userpswd){
        TMember member = userService.login(loginacct, userpswd);
        if (member==null){
            AppResponse response = AppResponse.fail(null);
            response.setMsg("用户名密码错误");
            return response;
        }
        String token = UUID.randomUUID().toString().replace("-", "");
        UserRspeonse userRspeonse = new UserRspeonse();
        /*存储在redis中，并设置有效期*/
        stringRedisTemplate.opsForValue().set(token,member.getId().toString(),2,TimeUnit.HOURS);
        userRspeonse.setAccessToken(token);
        BeanUtils.copyProperties(member,userRspeonse);
        return AppResponse.ok(userRspeonse);

    }

    @ApiOperation("获取一个用户信息")
    @GetMapping("/getUser/{id}")
    public AppResponse<UserRspeonse> getOneUser(@PathVariable("id") Integer id){
        System.out.println("userapp"+id);
        TMember member = userService.findById(id);
        UserRspeonse userRspeonse = new UserRspeonse();
        BeanUtils.copyProperties(member,userRspeonse);
        return AppResponse.ok(userRspeonse);
    }

    /*查询用户收件地址*/
    @GetMapping("/getAddress/{accessToken}")
    @ApiOperation(value = "获取用户收件地址")
    public AppResponse<List<TMemberAddress>> getAddress(@PathVariable("accessToken") String accessToken){
        String memberId = stringRedisTemplate.opsForValue().get(accessToken);
        if (StringUtils.isEmpty(memberId)){
            AppResponse<List<TMemberAddress>> fail = AppResponse.fail(null);
            fail.setMsg("用户没有登录");
            return fail;
        }
        List<TMemberAddress> address = userService.getAddress(Integer.parseInt(memberId));
        return AppResponse.ok(address);

    }
}
