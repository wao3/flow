package me.wangao.community.dao;

import me.wangao.community.entity.DiscussPost;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DiscussPostMapperTest {

    @Resource
    DiscussPostMapper discussPostMapper;

    @Test
    void selectDiscussPosts() {
        List<DiscussPost> discussPosts1 = discussPostMapper.selectDiscussPosts(null, 0, 10);
        discussPosts1.forEach(System.out::println);

        System.out.println("===============");
        List<DiscussPost> discussPosts2 = discussPostMapper.selectDiscussPosts(149, 0, 10);
        discussPosts2.forEach(System.out::println);
    }

    @Test
    void selectDiscussPostRows() {
        int allRows = discussPostMapper.selectDiscussPostRows(null);
        System.out.println("all rows: " + allRows);

        int rows149 = discussPostMapper.selectDiscussPostRows(149);
        System.out.println("id149 user's rows: " + rows149);
    }
}