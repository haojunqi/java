package com.dongyimai.page.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.dongyimai.mapper.TbGoodsDescMapper;
import com.dongyimai.mapper.TbGoodsMapper;
import com.dongyimai.mapper.TbItemCatMapper;
import com.dongyimai.mapper.TbItemMapper;
import com.dongyimai.pojo.*;
import dongyimai.page.service.ItemPageService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@Service
public class ItemPageServiceImpl implements ItemPageService {

    @Value("${pagedir}")
    private String pagedir;
    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;
    @Autowired
    private TbGoodsMapper tbGoodsMapper;
    @Autowired
    private TbGoodsDescMapper tbGoodsDescMapper;

    @Autowired
    private TbItemCatMapper tbItemCatMapper;

    @Autowired
    private TbItemMapper tbItemMapper;


    @Override
    public boolean deletePage(Long[] ids) {
        try {
            for (Long id : ids){
                new File(pagedir+id+".html").delete();
            }
            return  true;
        } catch (Exception e) {
            e.printStackTrace();
        return false;
        }
    }

    @Override
    public void cerateItemPage(Long goodsId) {
        /*获取*/
        Configuration configuration = freeMarkerConfigurer.getConfiguration();
        try {
            /*设定数据模板*/
            HashMap datamodel = new HashMap();
            Template template = configuration.getTemplate("item.ftl");
            TbGoods tbGoods = tbGoodsMapper.selectByPrimaryKey(goodsId);
            datamodel.put("goods",tbGoods);
            TbGoodsDesc tbGoodsDesc = tbGoodsDescMapper.selectByPrimaryKey(goodsId);
            datamodel.put("goodsDesc",tbGoodsDesc);
            /*查询item表中的规格数据*/
            TbItemExample tbItemExample = new TbItemExample();
            TbItemExample.Criteria criteria = tbItemExample.createCriteria();
            criteria.andStatusEqualTo("1");
            criteria.andGoodsIdEqualTo(goodsId);
            tbItemExample.setOrderByClause("is_default desc");
            List<TbItem> tbItems = tbItemMapper.selectByExample(tbItemExample);
            System.out.println(tbItems.size());
            datamodel.put("ItemList",tbItems);
            /*查询分类*/
            String tbItemCat = (String)tbItemCatMapper.selectByPrimaryKey(tbGoods.getCategory1Id()).getName();
            String tbItemCat2 = (String)tbItemCatMapper.selectByPrimaryKey(tbGoods.getCategory2Id()).getName();
            String tbItemCat3 = (String)tbItemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id()).getName();
            datamodel.put("itemCat1",tbItemCat);
            datamodel.put("itemCat2",tbItemCat2);
            datamodel.put("itemCat3",tbItemCat3);
            System.out.println("开始");
            /*创建输出流*/
            FileWriter fileWriter = new FileWriter(pagedir+goodsId+".html");
            System.out.println("完成");
            template.process(datamodel,fileWriter);

            /*关闭输出流*/
            fileWriter.close();
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }
    }
}
