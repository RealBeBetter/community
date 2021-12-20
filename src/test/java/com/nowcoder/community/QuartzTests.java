package com.nowcoder.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author : Real
 * @date : 2021/12/20 15:24
 * @description : Quartz 测试类
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class QuartzTests {

    @Autowired
    private Scheduler scheduler;

    @Test
    public void testDeleteScheduler() {
        try {
            boolean deleteJob = scheduler.deleteJob(new JobKey("alphaJob", "alphaJobGroup"));
            System.out.println(deleteJob);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

}
