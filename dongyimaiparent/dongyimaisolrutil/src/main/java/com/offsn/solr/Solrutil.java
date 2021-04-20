package com.offsn.solr;

import com.alibaba.fastjson.JSON;
import com.dongyimai.mapper.TbItemMapper;
import com.dongyimai.pojo.TbItem;
import com.dongyimai.pojo.TbItemExample;
import com.github.promeg.pinyinhelper.Pinyin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Solrutil {

    /*依赖tbIteam*/
    @Autowired
    private TbItemMapper tbItemMapper;

    @Autowired
    private SolrTemplate solrTemplate;


    public void updateSolr(){
        TbItemExample tbItemExample = new TbItemExample();
        TbItemExample.Criteria criteria = tbItemExample.createCriteria();
        criteria.andStatusEqualTo("1");
        List<TbItem> tbItems = tbItemMapper.selectByExample(tbItemExample);
        for (TbItem item: tbItems) {
            Map<String,String> SpecMap = JSON.parseObject(item.getSpec(), Map.class);
            HashMap<String,String> result=new HashMap<>();
            if (SpecMap!=null){
                for (String key : SpecMap.keySet()) {
                    String keypinyin = Pinyin.toPinyin(key, "");
                    result.put(keypinyin,SpecMap.get(key));
                }
            }
            item.setSpecMap(result);
        }
        solrTemplate.saveBeans(tbItems);
        solrTemplate.commit();

    }
    public static void main(String[] args) {
        ClassPathXmlApplicationContext classPathXmlApplicationContext = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext-*.xml");
        Solrutil solr = (Solrutil) classPathXmlApplicationContext.getBean("Solrutil");
        solr.updateSolr();
    }
}
