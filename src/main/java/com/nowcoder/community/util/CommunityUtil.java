package com.nowcoder.community.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * @ author : Real
 * @ date : 2021/11/14 20:27
 * @ description : 提供简单的工具类，声明为静态的，不由容器托管
 */
public class CommunityUtil {

    // 生成随机字符串，充当验证码使用
    public static String generatorUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    // 加密密码，使用 MD5 加密
    // 加密的时候，使用用户提供的密码 + salt 值进行加密，避免直接使用 MD5 逆向解密得到原始密码
    public static String md5(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

}
