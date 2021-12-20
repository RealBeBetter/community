package com.nowcoder.community.config;

import com.nowcoder.community.quartz.AlphaJob;
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
    // @Bean
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
    // @Bean
    public SimpleTriggerFactoryBean alphaSimpleTrigger(JobDetail alphaJobDetail) {
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(alphaJobDetail);
        factoryBean.setName("alphaTrigger");
        factoryBean.setGroup("alphaTriggerGroup");
        factoryBean.setRepeatInterval(3000);
        factoryBean.setJobDataMap(new JobDataMap());
        return factoryBean;
    }

}
