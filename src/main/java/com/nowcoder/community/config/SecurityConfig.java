package com.nowcoder.community.config;

import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import java.io.PrintWriter;

/**
 * @author : Real
 * @date : 2021/12/18 14:56
 * @description : Security 配置类
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter implements CommunityConstant {

    /**
     * 忽略对静态资源的拦截
     *
     * @param web web对象
     * @throws Exception 异常
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resources/**");
    }

    /**
     * 对请求的一些拦截处理
     *
     * @param http http 对象
     * @throws Exception 异常
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 授权
        http.authorizeRequests()
                .antMatchers(
                        "/user/setting",
                        "/user/upload",
                        "/discuss/add",
                        "/comment/add/**",
                        "/letter/**",
                        "/notice/**",
                        "/like",
                        "/follow",
                        "/unfollow"
                )
                .hasAnyAuthority(
                        AUTHORITY_ADMIN,
                        AUTHORITY_MODERATOR,
                        AUTHORITY_USER
                )
                .anyRequest().permitAll();
                // 禁用 CSRF 检查
                // .and().csrf().disable();

        // 无权限的处理
        http.exceptionHandling()
                .authenticationEntryPoint((httpServletRequest, httpServletResponse, e) -> {
                    // 没有登录的处理
                    String xRequestedWith = httpServletRequest.getHeader("x-requested-with");
                    if ("XMLHttpRequest".equals(xRequestedWith)) {
                        // 表示当前请求是异步请求，返回一个 JSON 字符串
                        httpServletResponse.setContentType("application/plain;charset=utf-8");
                        PrintWriter writer = httpServletResponse.getWriter();
                        writer.write(CommunityUtil.getJSONString(403, "您还没有登录，请先登录！"));
                    } else {
                        // 表示当前请求是一个同步请求，直接重定向登录页面
                        httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + "/login");
                    }
                })
                .accessDeniedHandler((httpServletRequest, httpServletResponse, e) -> {
                    // 没有权限的处理
                    String xRequestedWith = httpServletRequest.getHeader("x-requested-with");
                    if ("XMLHttpRequest".equals(xRequestedWith)) {
                        // 表示当前请求是异步请求，返回一个 JSON 字符串
                        httpServletResponse.setContentType("application/plain;charset=utf-8");
                        PrintWriter writer = httpServletResponse.getWriter();
                        writer.write(CommunityUtil.getJSONString(403, "您没有访问此功能的权限！"));
                    } else {
                        // 表示当前请求是一个同步请求，直接重定向权限不足的页面
                        httpServletResponse.sendRedirect("/denied");
                    }
                });

        // Security 会默认拦截 /logout 退出登录的逻辑，进行退出处理
        // 我们需要覆盖 Security 的默认逻辑，执行自定义的退出登录逻辑
        // 此语句的功能就是覆盖默认的拦截路径，使得自定义的 /logout 不被 Security 覆盖
        http.logout().logoutUrl("/securityLogout");
    }
}
