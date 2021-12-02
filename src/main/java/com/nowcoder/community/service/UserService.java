package com.nowcoder.community.service;

import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author : Real
 * @date : 2021/11/9 15:21
 * @description :
 */
@Service
public class UserService implements CommunityConstant {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    public User findUserById(int userId) {
        return userMapper.selectById(userId);
    }

    public String findUsername(int userId) {
        User user = userMapper.selectById(userId);
        return user.getUsername();
    }

    public Map<String, Object> register(User user) {
        Map<String, Object> map = new HashMap<>();
        // 空值处理
        if (user == null) {
            throw new IllegalArgumentException("参数不能为空！");
        }
        // 用户名为空
        if (StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMessage", "用户名不能为空！");
            return map;
        }
        // 密码为空
        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMessage", "密码不能为空！");
            return map;
        }
        // 邮箱为空
        if (StringUtils.isBlank(user.getEmail())) {
            map.put("emailMessage", "邮箱不能为空！");
            return map;
        }

        // 验证账号的合法性
        User selectUser = userMapper.selectByName(user.getUsername());
        if (selectUser != null) {
            // 账号用户名存在，表示应该更换用户名
            map.put("usernameMessage", "用户名已存在！");
            return map;
        }

        // 验证邮箱的合法性
        selectUser = userMapper.selectByEmail(user.getEmail());
        if (selectUser != null) {
            // 邮箱已经被注册，表示应该更换邮箱注册，或者找回密码
            map.put("emailMessage", "邮箱已被注册！");
        }

        // 注册账号，要将数据写入到数据库中
        // 一、设置 salt 值
        user.setSalt(CommunityUtil.generatorUUID().substring(0, 5));
        // 二、设置被加密的密码值
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        // 三、设置用户类型
        user.setType(0);
        // 四、设置用户状态，默认为未激活
        user.setStatus(0);
        // 五、设置用户的激活码
        user.setActivationCode(CommunityUtil.generatorUUID());
        // 六、设置默认头像路径
        user.setHeaderUrl("http://images.nowcoder.com/head/" + new Random().nextInt(1000) + "t.png");
        // 七、设置注册时间
        user.setCreateTime(new Date());
        // 八、保存用户
        userMapper.insertUser(user);
        // 九、发送激活邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        // 填充网页链接：http://locolhost:8080/community/activation/userId/activationCode
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);
        // 模板引擎调用网页，将其中的数据填充之后，生成一个 HTML 网页字符串对象，格式化网页
        String process = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(), "激活账号链接", process);

        return map;
    }

    public int activation(int userId, String activationCode) {
        // 查询到用户，获取到激活码，判断激活码是否正确
        User user = userMapper.selectById(userId);
        String selectCode = user.getActivationCode();
        if (user.getStatus() == 1) {
            // 表示已经激活过，重复激活
            return ACTIVATION_REPEAT;
        } else if (activationCode.equals(selectCode)) {
            // 激活码匹配，激活成功，修改激活状态
            userMapper.updateStatus(userId, 1);
            return ACTIVATION_SUCCESS;
        } else {
            // 激活码不匹配，应该返回失败
            return ACTIVATION_FAILED;
        }
    }

    /**
     * 登录方法，要求返回一个 Map ，表示用户登录的状态
     * 由于数据库存储的是加密的密码，
     * 所以在使用的时候要将传入的 password 进行加密之后和数据库中的密码进行比对
     * @param username 用户名
     * @param password 密码
     * @param expiredSeconds 失效时间
     * @return Map
     */
    public Map<String, Object> login(String username, String password, long expiredSeconds) {
        Map<String, Object> map = new HashMap<>();
        // 空值处理
        if (StringUtils.isBlank(username)) {
            map.put("usernameMessage", "用户名不能为空！");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("passwordMessage", "密码不能为空！");
            return map;
        }

        // 合法性验证，验证账号和密码是否合法
        User user = userMapper.selectByName(username);
        // 验证账号是否存在
        if (user == null) {
            map.put("usernameMessage", "该账号不存在！");
            return map;
        }
        // 验证账号是否已经激活
        if (user.getStatus() == 0) {
            map.put("usernameMessage", "该账号未激活，请先激活账号！");
        }

        // 验证密码
        password = CommunityUtil.md5(password + user.getSalt());
        if (!user.getPassword().equals(password)) {
            // 验证不为空的情况下且两者不相等，表示查询到该用户密码不正确
            map.put("passwordMessage", "密码不正确！");
            return map;
        }

        // 符合登录条件，生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setStatus(0);
        loginTicket.setTicket(CommunityUtil.generatorUUID());
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
        loginTicketMapper.insertLoginTicket(loginTicket);
        map.put("ticket", loginTicket.getTicket());
        return map;
    }

    public void logout(String ticket) {
        // 1 表示无效
        loginTicketMapper.updateStatus(ticket, 1);
    }

    public LoginTicket getLoginTicket(String ticket) {
        return loginTicketMapper.selectByTicket(ticket);
    }

    public int updateHeader(int userId, String header) {
        return userMapper.updateHeader(userId, header);
    }

    public int updatePassword(int userId, String password) {
        return userMapper.updatePassword(userId, password);
    }

}
