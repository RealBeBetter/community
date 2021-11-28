package com.nowcoder.community;

import com.nowcoder.community.util.SensitiveFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author : Real
 * @date : 2021/11/27 17:29
 * @description : 敏感词过滤测试
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
// 使用communityApplication.class类作为测试的配置类
@ContextConfiguration(classes = CommunityApplication.class)
public class SensitiveTests {

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void testSensitive() {
        // 赌博
        //***
        //吸毒
        //嫖娼
        //fabcd
        //abc
        //fabcc
        String text = "这里可以读博,可以嫖娼,可以吸毒, 可以***...";
        text = sensitiveFilter.filter(text);
        System.out.println(text);

        text = "这里可以赌→博→,可以→嫖→娼→,可以吸→毒, 可以→***→...fabc";
        text = sensitiveFilter.filter(text);
        System.out.println(text);

        text = "fabcd";
        text = sensitiveFilter.filter(text);
        System.out.println(text);

        text = "fabcc";
        text = sensitiveFilter.filter(text);
        System.out.println(text);

        text = "fabc";
        text = sensitiveFilter.filter(text);
        System.out.println(text);
    }

}
