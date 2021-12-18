package com.nowcoder.community.interceptor;

import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CookieUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Date;

/**
 * @author : Real
 * @date : 2021/11/18 22:55
 * @description : 登录凭证拦截器，主要是检查登录凭证以及存储登录用户
 */
@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 处理逻辑：主要是从 request 对象中将 Cookie 对象取出来，获得登录凭证
        String ticket = CookieUtil.getValue(request, "ticket");
        if (ticket != null) {
            // 表示存在登录凭证
            LoginTicket loginTicket = userService.getLoginTicket(ticket);
            // 检查登录凭证是否失效
            if (loginTicket != null && loginTicket.getStatus() == 0 & loginTicket.getExpired().after(new Date())) {
                // 根据凭证查询用户
                User user = userService.findUserById(loginTicket.getUserId());
                // 在本次请求中持有用户，使用 ThreadLocal 存储用户
                hostHolder.setUser(user);
                // 将用户授权结果存入 SecurityContext 中，便于 Security 进行授权
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        user, user.getPassword(), userService.getAuthorities(user.getId())
                );
                SecurityContextHolder.setContext(new SecurityContextImpl(authentication));
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 在处理 Controller 之后，获取用户对象，然后将用户对象填充到模板引擎中
        User user = hostHolder.getUser();
        if (user != null && modelAndView != null) {
            modelAndView.addObject("loginUser", user);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 在视图渲染之后执行，此时已经可以将 ThreadLocal 中的对象清除
        hostHolder.removeUser();
        // 将 SecurityContextHolder 也同样进行清理
        SecurityContextHolder.clearContext();
    }
}
