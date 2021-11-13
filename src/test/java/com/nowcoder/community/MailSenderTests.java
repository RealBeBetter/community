package com.nowcoder.community;

import com.nowcoder.community.util.MailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;

/**
 * @ author : Real
 * @ date : 2021/11/13 21:37
 * @ description :
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
// 使用communityApplication.class类作为测试的配置类
@ContextConfiguration(classes = CommunityApplication.class)
public class MailSenderTests {

    @Autowired
    private MailClient mailClient;

    // 使用模板引擎，主动获取 html 页面
    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void testSendMail() {
        mailClient.sendMail("2411178558@qq.com", "测试", new Date() + "   测试发送邮件");
    }

    @Test
    public void testSendHtmlMail () {
        Context context = new Context();
        context.setVariable("username", "Test 用户");
        // 模板引擎调用网页，将其中的数据填充之后，生成一个 HTML 网页字符串对象，格式化网页
        String process = templateEngine.process("/mail/demo", context);
        System.out.println(process);
        mailClient.sendMail("2411178558@qq.com", "HTML 测试", process);
    }
}
