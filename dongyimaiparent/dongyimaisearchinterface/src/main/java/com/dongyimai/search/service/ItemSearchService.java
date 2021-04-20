package com.dongyimai.search.service;

import com.dongyimai.pojo.TbItem;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {
    public Map<String,Object> search(Map searchMap);
    public Map searchBrandAndSpec(String category);

    /*审核通过的商品添加到solr*/
    public void ItemToSolr(List<TbItem> list);

    /*根据id删除solr中的数据*/
    public void deleteByIds(List goodsid);
}
