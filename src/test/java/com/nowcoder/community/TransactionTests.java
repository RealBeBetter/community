package com.nowcoder.community;

import com.nowcoder.community.service.ServiceDemo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author : Real
 * @date : 2021/12/2 21:23
 * @description : 事务测试
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = CommunityApplication.class)
public class TransactionTests {

    @Autowired
    private ServiceDemo serviceDemo;

    @Test
    public void testSave1() {
        System.out.println(serviceDemo.save1());
    }


    @Test
    public void testSave2() {
        System.out.println(serviceDemo.save2());
    }

}
