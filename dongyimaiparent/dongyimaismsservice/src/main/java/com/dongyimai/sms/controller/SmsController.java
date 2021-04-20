package com.dongyimai.sms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.jms.*;

@Controller
public class SmsController {

    @Autowired
    private Queue sms_queue;

    @Autowired
    private JmsTemplate jmsTemplate;

    @RequestMapping("/sendSms")
    public void sendSms(final String phone, final String code) {
        System.out.println(phone+""+code);
        jmsTemplate.send(sms_queue, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                MapMessage mapMessage = session.createMapMessage();
                mapMessage.setString("mobile", phone);
                mapMessage.setString("code", code);
                return mapMessage;
            }
        });
    }
}