package com.dongyimai.sellergoods.service;

import com.dongyimai.entity.PageResult;
import com.dongyimai.entity.Result;
import com.dongyimai.pojo.TbBrand;
import com.dongyimai.pojo.TbBrandExample;

import java.util.List;
import java.util.Map;

public interface BrandService {

    public List<TbBrand> findAll();

    /*定义一个展示所有品牌的分页方法*/
    public PageResult findPage(TbBrand tbBrand,int pageNum,int pageSize);


    /*添加品牌信息*/
    public int add(TbBrand tbBrand);

    /*添加修改品牌信息的方法*/
    public TbBrand findone(long id);
    public int update(TbBrand tbBrand);

    /*删除*/
    public int delete(Long ids[]);


    /*返回map集个数据*/
    public List<Map> select2BrandList();

}
