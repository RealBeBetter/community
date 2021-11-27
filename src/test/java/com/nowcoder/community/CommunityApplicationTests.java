package com.nowcoder.community;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.entity.DiscussPost;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author : Real
 * @date : 2021/11/3 20:49
 * @description :
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
// 使用communityApplication.class类作为测试的配置类
@ContextConfiguration(classes = CommunityApplication.class)
public class CommunityApplicationTests implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Test
    public void testApplicationContext () {
        System.out.println(applicationContext);
    }

    @Test
    public void testDiscussPostMapper() {
        List<DiscussPost> posts = discussPostMapper.selectDiscussPosts(0, 0, 10);
        for (DiscussPost post : posts) {
            System.out.println(post);
        }
    }

    @Test
    public void testSelectDiscussPostRows() {
        int i = discussPostMapper.selectDiscussPostRows(110);
        System.out.println(i);
    }

    // 实现ApplicationContextAware接口，用于获取IoC容器
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        // 将容器暂存到成员变量中，让成员变量获得容器，方便其他测试方法使用
        this.applicationContext = applicationContext;
    }

    @Test
    public void testSimpleDateFormat() {
        SimpleDateFormat simpleDateFormat = applicationContext.getBean(SimpleDateFormat.class);
        System.out.println(simpleDateFormat.format(new Date()));
    }
}
