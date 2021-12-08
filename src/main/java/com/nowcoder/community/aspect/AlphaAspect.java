package com.nowcoder.community.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

/**
 * @author : Real
 * @date : 2021/12/7 18:17
 * @description : Aspect demo ，注释掉
 */
//@Component
//@Aspect
public class AlphaAspect {

    /**
     * 匹配切点，使用 execution 表达式
     * 第一个 * 代表的是方法的返回值类型，之后写类的匹配路径，最后定位到具体的方法上
     * (..) 匹配的是方法的参数列表，这种写法表示匹配所有的参数列表
     */
    @Pointcut("execution(* com.nowcoder.community.service.*.*(..))")
    public void pointcut() {
        // 创建切点，使用表达式进行匹配
    }

    @Before("pointcut()")
    public void before() {
        System.out.println("before...");
    }

    @After("pointcut()")
    public void after() {
        System.out.println("after...");
    }

    @AfterReturning("pointcut()")
    public void afterReturning() {
        System.out.println("afterReturning...");
    }

    @AfterThrowing("pointcut()")
    public void afterThrowing() {
        System.out.println("afterThrowing...");
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        System.out.println("around before...");
        // 调用目标组件方法，一般会有一个返回值
        Object proceed = point.proceed();
        System.out.println("around after...");
        return proceed;
    }

}
