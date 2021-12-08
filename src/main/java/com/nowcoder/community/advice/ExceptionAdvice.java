package com.nowcoder.community.advice;

import com.nowcoder.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author : Real
 * @date : 2021/12/7 17:27
 * @description :
 */
@ControllerAdvice(annotations = Controller.class)   // 规定只扫描标注了 Controller 注解的类
public class ExceptionAdvice {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

    /**
     * 处理异常情况
     *
     * @param e        exception 对象
     * @param request  请求
     * @param response 应答
     */
    @ExceptionHandler({Exception.class})
    public void handleException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.error("服务器发生异常：" + e.getMessage());
        // 遍历发生异常之时的调用栈，并进行记录
        for (StackTraceElement element : e.getStackTrace()) {
            logger.error(element.toString());
        }

        // 判断请求的类型并且做出相应的处理
        String requestHeader = request.getHeader("x-requested-with");
        if ("XMLHttpRequest".equals(requestHeader)) {
            // 表示该请求返回的是 JSON 格式的字符串
            response.setContentType("application/plain;charset=utf-8");
            PrintWriter responseWriter = response.getWriter();
            // 将异常信息输出给 response 对象
            responseWriter.write(CommunityUtil.getJSONString(1, "服务器异常！"));
        } else {
            response.sendRedirect(request.getContextPath() + "/error");
        }
    }

}
