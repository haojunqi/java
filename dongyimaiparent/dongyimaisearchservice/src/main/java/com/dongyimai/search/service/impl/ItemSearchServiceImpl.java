package com.dongyimai.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.dongyimai.pojo.TbItem;
import com.dongyimai.search.service.ItemSearchService;
import com.github.promeg.pinyinhelper.Pinyin;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(timeout = 6000)
public class ItemSearchServiceImpl implements ItemSearchService {
    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    public void deleteByIds(List goodsid) {
        SimpleQuery simpleQuery = new SimpleQuery();
        Criteria criteria = new Criteria("item_goodsid");
        simpleQuery.addCriteria(criteria);
        solrTemplate.delete(simpleQuery);
        solrTemplate.commit();
    }

    @Override
    public void ItemToSolr(List<TbItem> list) {
        for(TbItem tbItem : list){
            Map<String,String > specMAP = JSON.parseObject(tbItem.getSpec(),Map.class);
            HashMap map = new HashMap();
            for (String key :specMAP.keySet()){
                map.put("item_spec_"+Pinyin.toPinyin(key,"").toLowerCase(),specMAP.get(key));
            }
            tbItem.setSpecMap(map);
        }
        System.out.println(list.size()+"方法执行");
        solrTemplate.saveBeans(list);
        solrTemplate.commit();
    }



    /*过滤查询*/


    private List searchCategorys(Map searchMap){
        List<Object> list = new ArrayList<>();
        SimpleQuery simpleQuery = new SimpleQuery();
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        simpleQuery.addCriteria(criteria);
        GroupOptions options = new GroupOptions();
        options.addGroupByField("item_category");
        simpleQuery.setGroupOptions(options);

        GroupPage<TbItem> page = solrTemplate.queryForGroupPage(simpleQuery, TbItem.class);
        GroupResult<TbItem> result = page.getGroupResult("item_category");
        Page<GroupEntry<TbItem>> entries = result.getGroupEntries();
        List<GroupEntry<TbItem>> content = entries.getContent();
        for (GroupEntry<TbItem> group: content) {
            list.add(group.getGroupValue());
        }

        return  list;

    }

    @Override
    public Map searchBrandAndSpec(String category) {
        Map map= new HashMap();
        Long typeId = (Long) redisTemplate.boundHashOps("itemcat").get(category);
        if (typeId != null){
            List brands = (List) redisTemplate.boundHashOps("brands").get(typeId);
            map.put("brands",brands);

            List specs = (List) redisTemplate.boundHashOps("specs").get(typeId);
            map.put("specs",specs);
        }
        return map;
    }

    @Override
    public Map<String, Object> search(Map searchMap) {
        Map result = new HashMap();
        /*查询高亮字体结果*/
        SimpleHighlightQuery highlightQuery = new SimpleHighlightQuery();
        HighlightOptions highlightOptions = new HighlightOptions();
        highlightOptions.addField("item_title");
        highlightOptions.setSimplePrefix("<font style='color:red'>");
        highlightOptions.setSimplePostfix("</font>");
        highlightQuery.setHighlightOptions(highlightOptions);

        /*查询条件*/
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        highlightQuery.addCriteria(criteria);
        /*条件筛选查询*/
        findByCatrgory(searchMap,highlightQuery);
        findByBrand(searchMap,highlightQuery);
        findBySpec(searchMap,highlightQuery);

        /*价格区间查询*/
        findByPrice(searchMap,highlightQuery);

        /*分页查询*/
        pageSearch(searchMap,highlightQuery);

        /*排序*/
        sort(searchMap,highlightQuery);
        /*完成高亮查询*/
        HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(highlightQuery, TbItem.class);
        result.put("totalPages",page.getTotalPages());
        result.put("totalElements",page.getTotalElements());
        List<HighlightEntry<TbItem>> highlighted = page.getHighlighted();
        for (HighlightEntry<TbItem> entry :highlighted){
            TbItem tbItem = entry.getEntity();

            /*查询出来的高亮字符串，和原来的数据进行替换*/
            List<HighlightEntry.Highlight> highlights = entry.getHighlights();
            if (highlights.size() >0 && highlights.get(0)!= null &&
            highlights.get(0).getSnipplets().get(0) != null){
            String highlTitle = highlights.get(0).getSnipplets().get(0);
            tbItem.setTitle(highlTitle);
            }

        }

        /*查询所有的商品分类*/
        List categorys = searchCategorys(searchMap);
        result.put("categorys",categorys);

        if (categorys!= null && categorys.size()>0){
            Map map = searchBrandAndSpec((String) categorys.get(0));
            result.putAll(map);
        }

        List<TbItem> content = page.getContent();
        result.put("rows",content);
        return result;
    }

    /*分类查询*/
    private void findByCatrgory(Map searchMap,SimpleHighlightQuery simpleHighlightQuery){
        if (searchMap.get("category") != null && ((String)searchMap.get("category")).length()>0){
            SimpleFilterQuery simpleFilterQuery = new SimpleFilterQuery();
            Criteria criteria = new Criteria("item_category").is(searchMap.get("category"));
            simpleFilterQuery.addCriteria(criteria);
            simpleHighlightQuery.addFilterQuery(simpleFilterQuery);
        }
    }


    /*品牌的过滤条件*/
    private void findByBrand(Map searchMap,SimpleHighlightQuery simpleHighlightQuery){
      if (searchMap.get("brand") != null && ((String)searchMap.get("brand")).length() >0){
        SimpleFilterQuery filterQuery = new SimpleFilterQuery();
        Criteria criteria = new Criteria("item_brand").is(searchMap.get("brand"));
        filterQuery.addCriteria(criteria);
        simpleHighlightQuery.addFilterQuery(filterQuery);
      }
    }

    /*规格过滤查询*/
    private void findBySpec(Map searchMap,SimpleHighlightQuery simpleHighlightQuery){
        if (searchMap.get("spec") != null){
        SimpleFilterQuery simpleFilterQuery = new SimpleFilterQuery();
        Map<String,String> spec = (Map) searchMap.get("spec");
        for (String key:spec.keySet()){
            Criteria criteria = new Criteria("item_spec_" + Pinyin.toPinyin(key, "").toLowerCase()).is(spec.get(key));
            simpleFilterQuery.addCriteria(criteria);
            simpleHighlightQuery.addFilterQuery(simpleFilterQuery);

        }
        }
    }


    /*过滤查询价格*/
    private void findByPrice(Map searchMap,SimpleHighlightQuery simpleHighlightQuery){
        if (!"".equals(searchMap.get("price"))){
            String price = (String) searchMap.get("price");
            String[] split = price.split("-");
            if (!split[0].equals("0")){
                Criteria criteria = new Criteria("item_price").greaterThanEqual(split[0]);
                SimpleFilterQuery simpleFilterQuery = new SimpleFilterQuery(criteria);
                simpleHighlightQuery.addFilterQuery(simpleFilterQuery);

            }
            if (!split[1].equals("*")){
                Criteria criteria = new Criteria("item_price").lessThanEqual(split[1]);
                SimpleFilterQuery simpleFilterQuery = new SimpleFilterQuery(criteria);
                simpleHighlightQuery.addFilterQuery(simpleFilterQuery);
            }
        }
    }

    /*分页*/
    private  void pageSearch(Map searchMap,SimpleHighlightQuery simpleHighlightQuery){
        Integer pageNo = (Integer)searchMap.get("pageNo");
        if (pageNo == null){
            pageNo=1;
        }
        Integer pageSize = (Integer) searchMap.get("pageSize");
        if(pageSize == null){
            pageSize=10;
        }

        simpleHighlightQuery.setOffset((pageNo-1)*pageSize);
        simpleHighlightQuery.setRows(pageSize);

    }

    /*排序*/
    private void sort(Map searhMap,SimpleHighlightQuery simpleHighlightQuery){
        /*获取需要排序的字段*/
        String  sortField = (String) searhMap.get("sortField");
        String  sort = (String) searhMap.get("sort");
        if (!StringUtils.isEmpty(sortField) && !StringUtils.isEmpty(sort)){
            if (sort.equalsIgnoreCase("asc")){
                System.out.println("方法执行");
                Sort orders = new Sort(Sort.Direction.ASC, "item_" + sortField);
                simpleHighlightQuery.addSort(orders);
            }
            if (sort.equalsIgnoreCase("desc")){
                Sort orders = new Sort(Sort.Direction.DESC, "item_" + sortField);
                simpleHighlightQuery.addSort(orders);
            }
        }
    }
}
