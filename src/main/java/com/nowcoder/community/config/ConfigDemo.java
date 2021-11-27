package com.nowcoder.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;

/**
 * @author : Real
 * @date : 2021/11/4 15:59
 * @description :
 */
@Configuration
public class ConfigDemo {

    /**
     * 标识一个第三方的类为 Bean，一般使用的是 @Bean 注解来完成
     * @return
     */
    @Bean
    public SimpleDateFormat simpleDateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

}
