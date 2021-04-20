package com.dongyimai.sms.listener;

import com.dongyimai.sms.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.HashMap;

@Component
public class SmsMessageListener implements MessageListener {
    @Autowired
    private SmsService smsService;

    @Override
    public void onMessage(Message message) {
        MapMessage mapMessage=(MapMessage)message;
        try {
            String mobile = mapMessage.getString("mobile");
            String code = mapMessage.getString("code");
            //String tpl_id = mapMessage.getString("tpl_id");
            HashMap<String, String> querys = new HashMap<>();
            querys.put("mobile",mobile);
            querys.put("param","code:" + code);
            querys.put("tpl_id","TP1711063");
            smsService.sendCode(querys);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
