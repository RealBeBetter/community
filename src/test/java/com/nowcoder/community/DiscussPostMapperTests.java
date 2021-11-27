package com.nowcoder.community;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.entity.DiscussPost;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * @author : Real
 * @date : 2021/11/9 0:08
 * @description :
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
// 使用communityApplication.class类作为测试的配置类
@ContextConfiguration(classes = CommunityApplication.class)
public class DiscussPostMapperTests {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Test
    public void testSelectPosts() {
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(0, 0, 10);
        for (DiscussPost discussPost : discussPosts) {
            System.out.println(discussPost);
        }

        int rows = discussPostMapper.selectDiscussPostRows(0);
        System.out.println(rows);
    }

}
