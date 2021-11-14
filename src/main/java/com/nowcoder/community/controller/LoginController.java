package com.nowcoder.community.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @ author : Real
 * @ date : 2021/11/14 19:27
 * @ description :
 */
@Controller
public class LoginController {

    @RequestMapping(path = "/register", method = RequestMethod.GET)
    // 获取注册页面
    public String getRegisterPage() {
        return "/site/register";
    }

}
