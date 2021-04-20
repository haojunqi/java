package com.dongyimai.sellergoods.service.listener;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dongyimai.mapper.TbGoodsMapper;
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
public class DeleteGoodsListener  implements MessageListener {
    @Autowired
    private TbGoodsMapper tbGoodsMapper;


    @Override
    public void onMessage(Message message) {
        ObjectMessage objectMessage=(ObjectMessage)message;
        try {
            Long[] ids = (Long[]) objectMessage.getObject();
            for (Long id : ids){
                /*删除商品数据*/
                System.out.println("执行sellergoods方法----------");
                TbGoods tbGoods = tbGoodsMapper.selectByPrimaryKey(id);
                tbGoods.setIsDelete("1");
                tbGoodsMapper.updateByPrimaryKey(tbGoods);
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
