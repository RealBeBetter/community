package com.nowcoder.community.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.File;

/**
 * @author : Real
 * @date : 2021/12/20 17:20
 * @description : wk 的配置类
 */
@Configuration
public class WkConfig {

    private static final Logger logger = LoggerFactory.getLogger(WkConfig.class);

    @Value("${wk.image.storage}")
    private String wkImageStorage;

    /**
     * 在服务启动的时候判断目录是否存在，如果不存在则将目录创建，避免发生错误
     */
    @PostConstruct
    public void init() {
        // 创建图片目录
        File file = new File(wkImageStorage);
        if (!file.exists()) {
            file.mkdir();
            logger.info("创建 wk 图片路径：" + wkImageStorage);
        }
    }

}
