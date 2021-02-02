package me.wangao.community.dao;

import me.wangao.community.entity.Message;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MessageMapperTest {

    @Resource
    private MessageMapper messageMapper;

    @Test
    void selectConversations() {
        List<Message> messages = messageMapper.selectConversations(111, 0, 20);
        messages.forEach(System.out::println);
    }

    @Test
    void selectConversationCount() {
        int count = messageMapper.selectConversationCount(111);
        System.out.println("conversation count: " + count);
    }

    @Test
    void selectLetters() {
        List<Message> messages = messageMapper.selectLetters("111_112", 0, 10);
        messages.forEach(System.out::println);
    }

    @Test
    void selectLetterCount() {
        int count = messageMapper.selectLetterCount("111_112");
        System.out.println("111_112 Letter count: " + count);
    }

    @Test
    void selectLetterUnreadCount() {
        int count = messageMapper.selectLetterUnreadCount(131, "111_131");
        System.out.println("131's 111_131 letter unread count: " + count);
    }
}