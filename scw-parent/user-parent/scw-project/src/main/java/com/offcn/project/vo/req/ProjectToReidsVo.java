package com.offcn.project.vo.req;

import com.offcn.project.pojo.TReturn;
import com.offcn.vo.BaseVo;
import lombok.Data;

import java.util.List;

@Data
public class ProjectToReidsVo extends BaseVo {
    /*项目的令牌*/
    private  String projectToken;
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
