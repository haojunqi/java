package com.offcn.project.vo.req;

import com.offcn.vo.BaseVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class ReturnInfoVo extends BaseVo {
    private String projectToken;
    @ApiModelProperty(value = "回报类型")
    private String type;

    @ApiModelProperty(value = "回报的支持金额")
    private Integer supportmoney;
    @ApiModelProperty(value = "回报的内容")
    private String content;
    @ApiModelProperty(value = "限制回报的数量")
    private Integer count;

    @ApiModelProperty(value = "单名限购")
    private Integer signalpurchase;

    @ApiModelProperty(value = "限购数量")
    private Integer purchase;

    @ApiModelProperty(value = "运费")
    private Integer freight;
    @ApiModelProperty(value = "发票")
    private String invoice;
    @ApiModelProperty(value = "回报时间")
    private Integer rtndate;
}
