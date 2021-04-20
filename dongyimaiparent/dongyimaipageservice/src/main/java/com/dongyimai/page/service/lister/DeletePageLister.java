package com.dongyimai.page.service.lister;

import dongyimai.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.io.File;
import java.io.Serializable;

@Component
public class DeletePageLister implements MessageListener {

    @Autowired
    private ItemPageService itemPageService;
    @Override
    public void onMessage(Message message) {
        ObjectMessage objectMessage=(ObjectMessage)message;
        try {
            Long[] ids = (Long[]) objectMessage.getObject();
            boolean b = itemPageService.deletePage(ids);
            System.out.println("删除"+b);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
