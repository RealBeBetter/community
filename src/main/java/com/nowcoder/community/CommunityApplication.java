package com.nowcoder.community;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

/**
 * @author : Real
 * @date : 2021/11/1 16:32
 * @description : 启动类
 */
@SpringBootApplication
public class CommunityApplication {

    /**
     * 解决 Netty 和 ES 的启动冲突，设置初始化后置方法
     */
    @PostConstruct
    public void init() {
        // 解决 Netty 启动冲突
        // see Netty4Utils.setAvailableProcessors
        System.setProperty("es.set.netty.runtime.available.processors", "false");
    }

    public static void main(String[] args) {
        SpringApplication.run(CommunityApplication.class, args);
    }

}
