package com.dongyimai.page.service.lister;

import dongyimai.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.io.Serializable;

@Component
public class createPageLister implements MessageListener {

    @Autowired
    private ItemPageService itemPageService;

    @Override
    public void onMessage(Message message) {
        ObjectMessage objectMessage=(ObjectMessage)message;
        try {
            Long[] ids = (Long[]) objectMessage.getObject();
            for (Long id : ids) {
                System.out.println(id);
                itemPageService.cerateItemPage(id);
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
