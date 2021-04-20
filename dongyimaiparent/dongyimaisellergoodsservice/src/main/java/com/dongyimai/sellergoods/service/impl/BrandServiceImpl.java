package com.dongyimai.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.dongyimai.entity.PageResult;
import com.dongyimai.entity.Result;
import com.dongyimai.mapper.TbBrandMapper;
import com.dongyimai.pojo.TbBrand;
import com.dongyimai.pojo.TbBrandExample;
import com.dongyimai.sellergoods.service.BrandService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


@Service
public class BrandServiceImpl implements BrandService {
    /*需要交互数据库*/
    @Autowired
    private TbBrandMapper tbBrandMapper;

    @Override
    public List<TbBrand> findAll() {
        return tbBrandMapper.selectByExample(null);
    }


    @Override
    public PageResult findPage(TbBrand tbBrand,int pageNum, int pageSize) {
        /*使用PageHeleper*/
        PageHelper.startPage(pageNum,pageSize);
        TbBrandExample tbBrandExample = new TbBrandExample();
        TbBrandExample.Criteria criteria = tbBrandExample.createCriteria();
        if (tbBrand != null){
            if(tbBrand.getName() != null && tbBrand.getName().length() >0){
                criteria.andNameLike("%"+tbBrand.getName()+"%");
            }
            if (tbBrand.getFirstChar() != null && tbBrand.getFirstChar().length()>0){
                criteria.andFirstCharEqualTo(tbBrand.getFirstChar());
            }
        }
        Page<TbBrand> page =(Page<TbBrand>) tbBrandMapper.selectByExample(tbBrandExample);
        return new PageResult(page.getTotal(),page.getResult());
    }


    /*添加品牌信息*/
    @Override
    public int add(TbBrand tbBrand) {
        return tbBrandMapper.insert(tbBrand);
    }

    @Override
    public TbBrand findone(long id) {
        return tbBrandMapper.selectByPrimaryKey(id);
    }

    @Override
    public int update(TbBrand tbBrand) {
        return tbBrandMapper.updateByPrimaryKey(tbBrand);
    }

    @Override
    public int delete(Long[] ids) {
        TbBrandExample tbBrandExample1 = new TbBrandExample();
        TbBrandExample.Criteria criteria = tbBrandExample1.createCriteria();
        criteria.andIdIn(Arrays.asList(ids));
        return tbBrandMapper.deleteByExample(tbBrandExample1);

    }

    @Override
    public List<Map> select2BrandList() {
        return tbBrandMapper.select2BrandList();
    }


}
