package com.offcn.test;

import com.offcn.user.UserApp;
import org.checkerframework.checker.units.qual.A;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.omg.CORBA.PRIVATE_MEMBER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {UserApp.class}) // 只有运行入口类才能进入测试的功能
public class UserAppTest {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void testStringRedis(){
        stringRedisTemplate.opsForValue().set("name3","小王");
    }

}
