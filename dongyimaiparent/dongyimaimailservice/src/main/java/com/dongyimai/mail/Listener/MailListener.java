package com.dongyimai.mail.Listener;


import com.dongyimai.mail.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.HashMap;
import java.util.Map;

@Component
public class MailListener implements MessageListener {

    @Autowired
    private MailService mailService;
    @Override
    public void onMessage(Message message) {
       MapMessage mapMessage = (MapMessage) message;
        try {
            String user = mapMessage.getString("user");
            String text = mapMessage.getString("text");
            HashMap<String, String> map = new HashMap<>();
            map.put("user",user);
            map.put("text",text);
            mailService.sendMail(map);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
