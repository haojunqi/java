package com.offcn.user.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel
@Data
public class UserRegisterVo {

    @ApiModelProperty("登录手机号")
    private String loginacct;

    @ApiModelProperty("登录密码")
    private String userpswd;
    @ApiModelProperty("登录名")
    private String username;
    @ApiModelProperty("邮箱")
    private String email;
    @ApiModelProperty("验证码")
    private String code;
}
