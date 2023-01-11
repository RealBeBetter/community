package com.nowcoder.community.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * @author : Real
 * @date : 2021/11/13 21:26
 * @description : 发送邮件客户端
 */
@Component
public class MailClient {

    private static final Logger logger = LoggerFactory.getLogger("Mail Logger");

    @Autowired
    private JavaMailSender mailSender;

    /**
     * 发送邮件的发送者用户名字
     */
    @Value("${spring.mail.username}")
    private String from;

    /**
     * 发送邮件
     *
     * @param to      目标
     * @param subject 主题
     * @param content 内容
     */
    public void sendMail(String to, String subject, String content) {
        // 使用 MimeMessage 构建邮件主体
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            // 加参数表示允许支持 html 文本
            helper.setText(content, true);
            // 使用 send 方法发送邮件
            mailSender.send(helper.getMimeMessage());
        } catch (MessagingException e) {
            logger.error("邮件发送失败" + e.getMessage());
            e.printStackTrace();
        }
    }

}
