package me.wangao.community.dao;

import me.wangao.community.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserMapperTest {

    @Resource
    UserMapper userMapper;

    @Test
    void select() {
        User user1 = userMapper.selectById(101);
        System.out.println(user1);

        User user2 = userMapper.selectByName("liubei");
        System.out.println(user2);

        User user3 = userMapper.selectByEmail("nowcoder101@sina.com");
        System.out.println(user3);
    }

    @Test
    void insert() {
        User user = new User();
        user.setUsername("test")
                .setPassword("123456")
                .setSalt("abc")
                .setEmail("test@qq.com")
                .setHeaderUrl("https://cdn.v2ex.com/avatar/8a8c/cca8/482014_large.png")
                .setCreateTime(new Date());

        int rows = userMapper.insertUser(user);
        System.out.println(rows);
        System.out.println(user.getId());
    }

    @Test
    void update() {
        int rows1 = userMapper.updateStatus(150, 0);
        System.out.println(rows1);

        int rows2 = userMapper.updateHeader(150, "https://cdn.v2ex.com/avatar/8a8c/cca8/482013_large.png");
        System.out.println(rows2);

        int rows3 = userMapper.updatePassword(150,"654321");
        System.out.println(rows2);
    }
}