package com.nowcoder.community.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * @author : Real
 * @date : 2021/11/20 20:37
 * @description : 从请求中获取 Cookie 对象值的小工具
 */
public class CookieUtil {

    /**
     * 获得Cookie
     *
     * @param request 请求
     * @param name    名字
     * @return {@link String}
     */
    public static String getValue(HttpServletRequest request, String name) {
        if (request == null || name == null) {
            throw new IllegalArgumentException("参数为空！");
        }
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            // 不为空才能开始遍历
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

}
