package com.dongyimai.mail.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class MailService {

    @Autowired
    private JavaMailSenderImpl sender;

    public void sendMail(Map<String,String> map){
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom("lyf980729@126.com");
        String user = map.get("user");
        String text = map.get("text");
        System.out.println(user+"---"+text);
        simpleMailMessage.setTo(user);
        simpleMailMessage.setText(text);
        System.out.println("发送邮件");
        try {
            sender.send(simpleMailMessage);
            System.out.println("发送成功");
        } catch (MailException e) {
            e.printStackTrace();
        }

    }
}
