package me.wangao.community.util;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MailClientTest {

    @Autowired
    private MailClient mailClient;

    @Test
    void sendMail() {
        mailClient.sendMail("1012717693@qq.com", "test", "test to unsubscribe");
    }
}