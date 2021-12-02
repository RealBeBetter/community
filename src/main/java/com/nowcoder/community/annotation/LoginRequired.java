package com.nowcoder.community.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author : Real
 * @date : 2021/11/27 15:49
 * @description : 登录检查注解
 */
@Target(ElementType.METHOD)  // 规定书写的位置为方法上
@Retention(RetentionPolicy.RUNTIME)  // 规定生效的时机为运行时
public @interface LoginRequired {
}
