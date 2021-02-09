package me.wangao.community.service;

import me.wangao.community.entity.DiscussPost;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DiscussPostServiceTest {

    @Resource
    private DiscussPostService discussPostService;

    @Test
    public void initDataForTest() {
        for (int i = 0; i < 300000; ++i) {
            DiscussPost post = new DiscussPost()
                    .setUserId(111)
                    .setTitle("互联网求职暖春计划")
                    .setContent("互联网求职暖春计划互联网求职暖春计划互联网求职暖春计划互联网求职暖春计划互联网求职暖春计划互联网求职暖春计划互联网求职暖春计划互联网求职暖春计划互联网求职暖春计划互联网求职暖春计划互联网求职暖春计划互联网求职暖春计划互联网求职暖春计划互联网求职暖春计划互联网求职暖春计划互联网求职暖春计划互联网求职暖春计划互联网求职暖春计划互联网求职暖春计划互联网求职暖春计划互联网求职暖春计划互联网求职暖春计划互联网求职暖春计划互联网求职暖春计划互联网求职暖春计划互联网求职暖春计划")
                    .setCreateTime(new Date())
                    .setScore(Math.random() * 2000);

            discussPostService.addDiscussPost(post);
        }
    }

    @Test
    void findDiscussPosts() {

        System.out.println(discussPostService.findDiscussPosts(null, 0, 10, 1));
        System.out.println(discussPostService.findDiscussPosts(null, 0, 10, 1));
        System.out.println(discussPostService.findDiscussPosts(null, 0, 10, 1));
        System.out.println(discussPostService.findDiscussPosts(null, 0, 10, 0));
    }

    @Test
    void findDiscussPostRows() {
    }
}