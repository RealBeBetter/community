package com.nowcoder.community.config;

import com.nowcoder.community.interceptor.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @ author : Real
 * @ date : 2021/11/18 20:44
 * @ description :
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private InterceptorDemo interceptorDemo;

    @Autowired
    private LoginTicketInterceptor loginTicketInterceptor;

    @Autowired
    private DataInterceptor dataInterceptor;

    /**
     * 弃用原先的登录鉴权方案，使用 Spring Security 解决权限管理
     * @Autowired
     *     private LoginRequiredInterceptor loginRequiredInterceptor;
     */

    @Autowired
    private MessageInterceptor messageInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptorDemo)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg")
                .addPathPatterns("/register", "/login");

        // 除了静态资源都需要拦截
        registry.addInterceptor(loginTicketInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg");

        // 添加登录鉴权，配置登录检验拦截器
        //registry.addInterceptor(loginRequiredInterceptor)
        //        .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg");

        // 添加未读消息拦截，实时显示未读消息数量
        registry.addInterceptor(messageInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg");

        // 添加用户请求拦截，记录网站 DAU 以及 UV 数据
        registry.addInterceptor(dataInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg");
    }
}
