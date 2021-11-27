package com.nowcoder.community.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author : Real
 * @date : 2021/11/18 20:32
 * @description :
 */
@Component
public class InterceptorDemo implements HandlerInterceptor {

    public static final Logger logger = LoggerFactory.getLogger(InterceptorDemo.class);

    // 发生在 Controller 访问之前
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        logger.debug("preHandle " + handler.toString());
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    // 发生在 Controller 访问之后，视图渲染完成之前
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        logger.debug("postHandle " + handler.toString());
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    // 发生在 模板引擎加载之后 / 视图渲染完毕之后
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        logger.debug("afterCompletion " + handler.toString());
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
