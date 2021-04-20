package com.dongyimai.search.service.listener;

import com.dongyimai.pojo.TbGoods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.SolrDataQuery;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

@Component
public class DeleteGoodsListener implements MessageListener {
    @Autowired
    private SolrTemplate solrTemplate;


    @Override
    public void onMessage(Message message) {
        ObjectMessage objectMessage=(ObjectMessage)message;
        try {
            Long[] ids = (Long[]) objectMessage.getObject();
            /*根据id删除solr中的数据*/
            System.out.println("执行searchservice方法++++++++++");
            SimpleQuery simpleQuery = new SimpleQuery();
            Criteria criteria = new Criteria("item_goodsid").is(ids);
            SolrDataQuery solrDataQuery = simpleQuery.addCriteria(criteria);
            solrTemplate.delete(solrDataQuery);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
