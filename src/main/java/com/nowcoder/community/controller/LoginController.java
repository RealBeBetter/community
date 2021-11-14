package com.nowcoder.community.controller;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

/**
 * @ author : Real
 * @ date : 2021/11/14 19:27
 * @ description :
 */
@Controller
public class LoginController implements CommunityConstant {

    @Autowired
    private UserService userService;

    // 获取注册页面
    @RequestMapping(path = "/register", method = RequestMethod.GET)
    public String getRegisterPage() {
        return "/site/register";
    }

    // 获取登录页面
    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public String getLoginPage() {
        return "/site/login";
    }

    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public String register(Model model, User user) {
        Map<String, Object> register = userService.register(user);
        if (register == null || register.isEmpty()) {
            // 注册成功
            model.addAttribute("message", "注册成功！我们已经向您的邮箱发送了一封激活邮件，请点击邮件链接激活");
            model.addAttribute("target", "/index");
            return "/site/operate-result";
        } else {
            model.addAttribute("usernameMessage", register.get("usernameMessage"));
            model.addAttribute("passwordMessage", register.get("passwordMessage"));
            model.addAttribute("emailMessage", register.get("emailMessage"));
            return "/site/register";
        }
    }

    // 填充网页链接：http://locolhost:8080/community/activation/userId/activationCode
    // 直接访问路径，使用 GET 方式即可
    @RequestMapping(path = "/activation/{userId}/{activationCode}", method = RequestMethod.GET)
    public String activation(Model model,
                             @PathVariable("userId") int userId,
                             @PathVariable("activationCode") String activationCode) {
        int activation = userService.activation(userId, activationCode);
        // 结果表示请求的链接是否完成了激活操作
        if (activation == ACTIVATION_SUCCESS) {
            // 激活成功
            model.addAttribute("message", "激活成功！您的账号已经可以正常使用了！");
            model.addAttribute("target", "/login");
        } else if (activation == ACTIVATION_FAILED) {
            // 激活失败
            model.addAttribute("message", "激活失败！请检查您的激活链接是否正确！");
            model.addAttribute("target", "/index");
        } else if (activation == ACTIVATION_REPEAT) {
            // 重复激活
            model.addAttribute("message", "无效操作！该账号已经进行了激活！");
            model.addAttribute("target", "/index");
        }
        return "/site/operate-result";

    }

}
