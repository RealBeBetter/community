package com.nowcoder.community;

import com.nowcoder.community.service.ServiceDemo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author : Real
 * @date : 2021/12/20 13:51
 * @description : 线程池测试类
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class ThreadPoolTests {

    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolTests.class);

    /**
     * jdk 普通线程池
     */
    private final ExecutorService executorService = Executors.newFixedThreadPool(5);

    /**
     * jdk 可执行定时任务的线程池
     */
    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);

    /**
     * Spring 普通线程池
     */
    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    /**
     * Spring 可执行定时任务的线程池
     */
    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;

    @Autowired
    private ServiceDemo serviceDemo;

    /**
     * 休眠方法，避免 Test 方法直接结束
     */
    private void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * jdk 普通任务线程池
     */
    @Test
    public void testExecutorService() {
        Runnable task = () -> logger.debug("Hello ExecutorService");

        for (int i = 0; i < 10; i++) {
            executorService.submit(task);
        }
        sleep(10000);
    }

    /**
     * jdk 可执行定时任务的线程池
     */
    @Test
    public void testScheduledExecutorService() {
        Runnable task = () -> logger.debug("Hello ScheduledExecutorService");

        // 规定以何种频率执行定时线程
        scheduledExecutorService.scheduleAtFixedRate(task, 10000, 1000, TimeUnit.MILLISECONDS);

        sleep(30000);
    }

    /**
     * 测试 Spring 普通线程池
     */
    @Test
    public void testThreadPoolTaskExecutor() {
        Runnable task = () -> logger.debug("Hello ThreadPoolTaskExecutor");
        for (int i = 0; i < 10; i++) {
            taskExecutor.submit(task);
        }
        sleep(10000);
    }

    /**
     * 测试 Spring 定时任务线程池
     */
    @Test
    public void testThreadPoolTaskScheduler() {
        Runnable task = () -> logger.debug("Hello ThreadPoolTaskScheduler");
        Date startTime = new Date(System.currentTimeMillis() + 10000);
        taskScheduler.scheduleAtFixedRate(task, startTime, 1000);
        sleep(30000);
    }

    /**
     * Spring 线程池的简化调用方法
     */
    @Test
    public void testThreadPoolTaskExecutorSimple() {
        for (int i = 0; i < 10; i++) {
            serviceDemo.execute1();
        }

        sleep(10000);
    }

    /**
     * Spring 线程池的定时任务简化调用方法
     */
    @Test
    public void testThreadPoolTaskSchedulingSimple() {
        /*for (int i = 0; i < 10; i++) {
            serviceDemo.execute2();
        }*/

        // 会自动执行
        sleep(30000);
    }


}
