package com.nowcoder.community.controller;

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

import javax.servlet.ServletOutputStream;
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

    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String getSettingPage() {
        return "/site/setting";
    }

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
}
