package com.nowcoder.community.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author : Real
 * @date : 2021/12/20 14:18
 * @description : 线程池配置类
 */
@Configuration
@EnableScheduling
@EnableAsync
public class ThreadPoolConfig {
}
