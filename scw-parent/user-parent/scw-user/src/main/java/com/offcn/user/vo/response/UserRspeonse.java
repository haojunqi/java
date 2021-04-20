package com.offcn.user.vo.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "用户响应信息")
public class UserRspeonse {
    @ApiModelProperty(value = "令牌的标识，登录访问都需要携带该令牌")
    private String accessToken;//令牌

    private String loginacct;

    private String userpswd;

    private String username;

    private String email;

    private String authstatus;

    private String usertype;

    private String realname;

    private String cardnum;

    private String accttype;

}
