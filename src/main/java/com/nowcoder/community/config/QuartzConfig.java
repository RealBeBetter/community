package com.nowcoder.community.config;

import com.nowcoder.community.quartz.AlphaJob;
import com.nowcoder.community.quartz.PostScoreRefreshJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

/**
 * @author : Real
 * @date : 2021/12/20 14:58
 * @description : 定时任务配置类，配置数据库调用
 */
@Configuration
public class QuartzConfig {


    /**
     * FactoryBean 可简化 Bean 的实例化过程
     * 1. 通过 FactoryBean 封装 Bean 的实例化过程
     * 2. 将 FactoryBean 注入到 IOC 容器中
     * 3. 将 FactoryBean 注入给其他的 Bean
     * 4. 该 Bean 得到的是 FactoryBean 管理的对象实例
     *
     * @return JobDetailFactoryBean
     */
    public JobDetailFactoryBean alphaJobDetail() {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(AlphaJob.class);
        factoryBean.setName("alphaJob");
        factoryBean.setGroup("alphaJobGroup");
        factoryBean.setDurability(true);
        factoryBean.setRequestsRecovery(true);
        return factoryBean;
    }

    /**
     * 配置 trigger ：SimpleTriggerFactoryBean / CronTriggerFactoryBean
     *
     * @param alphaJobDetail JobDetail 对象
     * @return SimpleTriggerFactoryBean
     */
    public SimpleTriggerFactoryBean alphaSimpleTrigger(JobDetail alphaJobDetail) {
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(alphaJobDetail);
        factoryBean.setName("alphaTrigger");
        factoryBean.setGroup("alphaTriggerGroup");
        factoryBean.setRepeatInterval(3000);
        factoryBean.setJobDataMap(new JobDataMap());
        return factoryBean;
    }


    /**
     * 刷新帖子分数任务配置
     *
     * @return {@link JobDetailFactoryBean}
     */
    @Bean
    public JobDetailFactoryBean postScoreRefreshJobDetail() {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(PostScoreRefreshJob.class);
        factoryBean.setName("postScoreRefreshJob");
        factoryBean.setGroup("communityJobGroup");
        factoryBean.setDurability(true);
        factoryBean.setRequestsRecovery(true);
        return factoryBean;
    }

    /**
     * 分数刷新后触发
     *
     * @param postScoreRefreshJobDetail 文章分数刷新工作细节
     * @return {@link SimpleTriggerFactoryBean}
     */
    @Bean
    public SimpleTriggerFactoryBean postScoreRefreshTrigger(JobDetail postScoreRefreshJobDetail) {
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(postScoreRefreshJobDetail);
        factoryBean.setName("postScoreRefreshTrigger");
        factoryBean.setGroup("communityTriggerGroup");
        factoryBean.setRepeatInterval(1000 * 60 * 5);
        factoryBean.setJobDataMap(new JobDataMap());
        return factoryBean;
    }

}
