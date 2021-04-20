package com.offcn.project.vo.req;

import com.offcn.project.pojo.TReturn;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

@ApiModel
@Data
public class ProjectBaseInfoVo {
    /*创建新的众筹项目时提交的基本信息*/
    //项目的令牌
    private String projectToken;
    private String accesstoken;
    private String name;

    private String remark;

    private Long money;

    private Integer day;
    private Integer memberid;
    private String headerImage;
    /*商品的详情图片*/
    private List<String> detailsImages;
    private List<Integer> typeIds;
    private List<Integer> tagIds;
    private List<TReturn> projectReturns;

}
