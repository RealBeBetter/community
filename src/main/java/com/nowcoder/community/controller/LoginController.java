package com.nowcoder.community.controller;

import com.google.code.kaptcha.Producer;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;

/**
 * @author : Real
 * @date : 2021/11/14 19:27
 * @description : 登录页面
 */
@Controller
public class LoginController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private Producer kaptchaProducer;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    /**
     * 获取注册页面
     */
    @RequestMapping(path = "/register", method = RequestMethod.GET)
    public String getRegisterPage() {
        return "/site/register";
    }

    /**
     * 获取登录页面
     */
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

    // 登录的页面中，再自动访问该路径，返回生成的验证码图片
    @RequestMapping(path = "/kaptcha", method = RequestMethod.GET)
    public void getKaptcha(HttpServletResponse response, HttpSession session) {
        // 生成验证码并返回
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);

        // 将验证码存入 Session
        session.setAttribute("kaptcha", text);

        // 将图片输出给浏览器
        response.setContentType("image/png");
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            ImageIO.write(image, "png", outputStream);

        } catch (IOException e) {
            logger.error("响应验证码失败：" + e.getMessage());
        }
    }

    /**
     * 登录方法，使用的 URL 路径可以和之前相同，只要方法不相同就不会发生冲突
     */
    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public String login(String username, String password, String code, boolean rememberMe,
                        HttpSession session, HttpServletResponse response, Model model) {
        // 检查验证码
        String kaptcha = session.getAttribute("kaptcha").toString();
        if (StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !kaptcha.equalsIgnoreCase(code)) {
            // 验证码错误
            model.addAttribute("codeMessage", "验证码错误！");
            return "/site/login";
        }

        // 检查账号，密码
        int expiredSeconds = rememberMe ? REMEMBER_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;
        Map<String, Object> map = userService.login(username, password, expiredSeconds);
        if (map.containsKey("ticket")) {
            // 表示登录成功，需要让客户端携带登录凭证
            // 这个 ticket 应该使用 Cookie 存放到客户端
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
            cookie.setPath(contextPath);
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);
            return "redirect:/index";
        } else {
            // 登录失败，将提示信息展示到前台页面
            // 如果不是相应的错误类型，那么获取到的数据也是空值，不会显示
            model.addAttribute("usernameMessage", map.get("usernameMessage"));
            model.addAttribute("passwordMessage", map.get("passwordMessage"));
            return "/site/login";
        }
    }

    @RequestMapping(path = "/logout", method = RequestMethod.GET)
    public String logout(@CookieValue("ticket") String ticket) {
        userService.logout(ticket);
        return "redirect:/login";
    }

}
