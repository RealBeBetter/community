package com.nowcoder.community.controller;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @ author : Real
 * @ date : 2021/11/20 22:11
 * @ description :
 */
@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @LoginRequired
    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String getSettingPage() {
        return "/site/setting";
    }

    @LoginRequired
    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model) {
        // 异常情况处理
        if (headerImage == null) {
            model.addAttribute("error", "您还没有添加图片！");
            return "/site/setting";
        }

        // 获取文件名
        String filename = headerImage.getOriginalFilename();
        String filetype = "";
        // 判断文件类型
        if (!StringUtils.isBlank(filename)) {
            // 生成的文件后缀名格式一般为 .jpg / .png / .jpeg 格式
            filetype = filename.substring(filename.lastIndexOf("."));
        }

        // 文件格式正确性判断
        if (StringUtils.isBlank(filetype)) {
            /*if ("jpg".equals(filetype) || "jpeg".equals(filetype) || "png".equals(filetype)) {

            } else {

            }*/
            model.addAttribute("error", "文件格式不正确！");
            return "/site/setting";
        }

        // 生成随机文件名
        filename = CommunityUtil.generatorUUID() + filetype;
        // 确定文件存放的路径
        File file = new File(uploadPath + "/" + filename);
        // 将文件存储到目标文件夹中
        try {
            headerImage.transferTo(file);
        } catch (IOException e) {
            logger.error("头像图片存储失败：" + e.getMessage());
            throw new RuntimeException("上传头像失败！服务器异常发生异常", e);
        }

        // 存储成功之后要更新用户的头像路径
        // 更新之后的路径应该为 web 访问路径
        // http://localhost:8080/community/user/header/xxx.jpg
        User user = hostHolder.getUser();
        user = userService.findUserById(user.getId());
        String headerUrl = domain + contextPath + "/user/header/" + filename;
        // 输出测试头像路径
        // System.out.println("更新之后的头像路径： " + headerUrl);
        int updateHeader = userService.updateHeader(user.getId(), headerUrl);
        /*if (updateHeader == 1) {
            model.addAttribute("message", "头像修改成功！");
        }*/
        // 重定向会重新执行 Controller 中的 RequestMapping 映射请求
        return "redirect:/index";
    }

    @LoginRequired
    @RequestMapping(path = "/header/{fileName}", method = RequestMethod.GET)
    public void getHeaderUrl(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        // 服务器存放路径
        fileName = uploadPath + "/" + fileName;
        // 获得 .jpg / .png / .jpeg 类型的字符串，文件后缀名
        String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
        // 响应文件类型
        response.setContentType("image/" + fileType);

        // 将图片使用输出流写入 response 对象
        try (
                OutputStream outputStream = response.getOutputStream();
                FileInputStream fileInputStream = new FileInputStream(fileName);
        ) {
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败：" + e.getMessage());
        }
    }

    @LoginRequired
    @RequestMapping(path = "/updatePassword", method = RequestMethod.POST)
    public String updatePassword(String oldPassword, String newPassword, String confirmPassword, Model model) {
        // 判断两次输入的新密码是否相等，是否合法
        if (!StringUtils.isBlank(newPassword) || !StringUtils.isBlank(confirmPassword)) {
            if (!newPassword.equals(confirmPassword)) {
                // 两次密码不相等
                model.addAttribute("confirmPasswordError", "确认密码不一致！请重新输入！");
                return "/site/setting";
            }
        }/* else {
            // 前端界面做了判断，不需要这里的处理
            if (StringUtils.isBlank(newPassword)) {
                model.addAttribute("newPasswordError", "新密码为空！请重新输入！");
            } else {
                model.addAttribute("confirmPasswordError", "确认密码为空！请重新输入！");
            }
            return "/site/setting";
        }*/

        // 获得原始密码，判断初始密码是否正确
        User user = hostHolder.getUser();
        if (oldPassword.length() == 0) {
            // 原始密码为空，应该添加提示信息
            model.addAttribute("initialError", "原密码为空！请重新输入！");
            return "/site/setting";
        }
        oldPassword = CommunityUtil.md5(oldPassword + user.getSalt());
        if (!StringUtils.isBlank(oldPassword)) {
            if (!oldPassword.equals(user.getPassword())) {
                model.addAttribute("initialError", "原密码错误！请重新输入！");
                return "/site/setting";
            }
        }

        // 更新密码
        userService.updatePassword(user.getId(), CommunityUtil.md5(newPassword + user.getSalt()));
        // 修改成功重定向至登录界面，并且设置原有的 LoginTicket 失效
        return "redirect:/logout";
    }

}