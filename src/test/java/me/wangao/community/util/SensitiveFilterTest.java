package me.wangao.community.util;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SensitiveFilterTest {

    @Resource
    SensitiveFilter sensitiveFilter;

    @Test
    void filter() {
        String text = "这里不能赌博，不能吸毒，不能嫖娼，不能开票";
        String filterText = sensitiveFilter.filter(text);
        System.out.println(filterText);

        String text2 = "这里不能❤赌❤博❤，不能❤吸❤毒❤，不能❤嫖❤娼❤，不能❤开❤票❤";
        String filterText2 = sensitiveFilter.filter(text2);
        System.out.println(filterText2);
    }
}