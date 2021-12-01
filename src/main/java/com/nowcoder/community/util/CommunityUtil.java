package com.nowcoder.community.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author : Real
 * @date : 2021/11/14 20:27
 * @description : 提供简单的工具类，声明为静态的，不由容器托管
 */
public class CommunityUtil {

    /**
     * 生成随机字符串，充当验证码使用
     */
    public static String generatorUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 加密密码，使用 MD5 加密
     * 加密的时候，使用用户提供的密码 + salt 值进行加密，避免直接使用 MD5 逆向解密得到原始密码
     */
    public static String md5(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    /*public static void main(String[] args) {
        // String s = md5("12327dc0");     // 68bb6c4ff160bbf37d0dcdd90692786a
        String s = md5("1227dc0");     // 5a7efd31b0d9c0fb9f5596b4daece9ee

        System.out.println(s);
    }*/

    public static String getJSONString(int code, String message, Map<String, Object> map) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("message", message);
        if (map != null) {
            for (String key : map.keySet()) {
                json.put(key, map.get(key));
            }
        }
        return json.toJSONString();
    }

    public static String getJSONString(int code, String message) {
        return getJSONString(code, message, null);
    }

    public static String getJSONString(int code) {
        return getJSONString(code, null, null);
    }

    public static void main(String[] args) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "zhangsan");
        map.put("age", 20);
        System.out.println(getJSONString(0, "OK", map));
    }

}
