package me.wangao.community.dao;

import me.wangao.community.entity.LoginTicket;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LoginTicketMapperTest {

    @Resource
    LoginTicketMapper loginTicketMapper;

    @Test
    void insertLoginTicket() {
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(101)
                .setTicket("abc")
                .setStatus(0)
                .setExpired(new Date(System.currentTimeMillis() + 1000 * 60 * 10));

        int i = loginTicketMapper.insertLoginTicket(loginTicket);
        System.out.println(loginTicket);
        assertNotNull(loginTicket.getId());
    }

    @Test
    void selectAndUpdate() {
        LoginTicket loginTicket = loginTicketMapper.selectByTicket("abc");
        assertNotNull(loginTicket);
        System.out.println(loginTicket);

        int i = loginTicketMapper.updateStatus("abc", 1);
        assertEquals(i, 1);
        LoginTicket loginTicket2 = loginTicketMapper.selectByTicket("abc");
        System.out.println(loginTicket2);
        assertEquals(loginTicket2.getStatus(), 1);
    }
}