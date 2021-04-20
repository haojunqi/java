package com.offcn.order.reqvo;


import lombok.Data;

@Data
public class OrderInfoSubmitVo {
    private String accessToken;
    private Integer projectid;

    private Integer returnid;

    private Integer rtncount;

    private String address;

    private String invoice;

    private String invoictitle;

    private String remark;
}
