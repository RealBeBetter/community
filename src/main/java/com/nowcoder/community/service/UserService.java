package com.nowcoder.community.service;

import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.MailClient;
import com.nowcoder.community.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author : Real
 * @date : 2021/11/9 15:21
 * @description : 用户的 Service 层
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
    private RedisTemplate redisTemplate;

    // @Autowired
    // private LoginTicketMapper loginTicketMapper;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    public User findUserById(int userId) {
        // return userMapper.selectById(userId);
        User user = getCacheUser(userId);
        if (user == null) {
            user = initCacheUser(userId);
        }
        return user;
    }

    public String findUsername(int userId) {
        /*User user = userMapper.selectById(userId);
        return user.getUsername();*/
        User user = getCacheUser(userId);
        if (user == null) {
            user = initCacheUser(userId);
        }
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
            clearCacheUser(userId);
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
     *
     * @param username       用户名
     * @param password       密码
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
        // loginTicketMapper.insertLoginTicket(loginTicket);
        // 将登录凭证转存到 redis 中
        String ticketKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(ticketKey, loginTicket);

        map.put("ticket", loginTicket.getTicket());
        return map;
    }

    public void logout(String ticket) {
        // 1 表示无效
        // loginTicketMapper.updateStatus(ticket, 1);

        String ticketKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(ticketKey);
        loginTicket.setStatus(1);
        redisTemplate.opsForValue().set(ticketKey, loginTicket);
    }

    public LoginTicket getLoginTicket(String ticket) {
        // return loginTicketMapper.selectByTicket(ticket);
        String ticketKey = RedisKeyUtil.getTicketKey(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(ticketKey);
    }

    public int updateHeader(int userId, String header) {
        int rows = userMapper.updateHeader(userId, header);
        clearCacheUser(userId);
        return rows;
    }

    public int updatePassword(int userId, String password) {
        int rows = userMapper.updatePassword(userId, password);
        clearCacheUser(userId);
        return rows;
    }

    /**
     * 发送忘记密码邮件
     *
     * @param email 目标邮箱
     * @param code  验证码
     * @return 发送邮件的时间，需要在五分钟之内重置密码才能有效
     */
    public void sendForgetPasswordVerify(String email, String code) {
        // 发送验证码邮件
        Context context = new Context();
        context.setVariable("email", email);
        context.setVariable("verify", code);
        // 模板引擎调用网页，将其中的数据填充之后，生成一个 HTML 网页字符串对象，格式化网页
        String process = templateEngine.process("/mail/forget", context);
        mailClient.sendMail(email, "牛客网-忘记密码", process);
    }

    public User findUserByName(String username) {
        return userMapper.selectByName(username);
    }

    public User findUserByEmail(String email) {
        return userMapper.selectByEmail(email);
    }

    /**
     * 1. 优先从缓存中取值
     *
     * @param userId 用户 ID
     * @return user 对象
     */
    private User getCacheUser(int userId) {
        String userKey = RedisKeyUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(userKey);
    }

    /**
     * 2. 取不到时初始化缓存数据
     *
     * @param userId 用户 ID
     * @return user 对象
     */
    private User initCacheUser(int userId) {
        User user = userMapper.selectById(userId);
        String userKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(userKey, user, 3600, TimeUnit.SECONDS);
        return user;
    }

    /**
     * 3. 数据变更时清除缓存数据
     *
     * @param userId 用户 ID
     */
    private void clearCacheUser(int userId) {
        String userKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(userKey);
    }

    /**
     * 获得用户的权限
     *
     * @return List<GrantedAuthority> 权限列表
     */
    public Collection<? extends GrantedAuthority> getAuthorities(int userId) {
        User user = this.findUserById(userId);
        // 获得数据库中的用户权限字段
        List<GrantedAuthority> list = new ArrayList<>();
        list.add((GrantedAuthority) () -> {
            // 获得用户的权限
            switch (user.getType()) {
                case 1:
                    return AUTHORITY_ADMIN;
                case 2:
                    return AUTHORITY_MODERATOR;
                default:
                    return AUTHORITY_USER;
            }
        });
        /*list.add((GrantedAuthority) () -> {
            if (list.contains(AUTHORITY_MODERATOR)) {
                // 判断是版主，可能为作者
                // 业务在帖子详情控制器页面编写
            }
            if (list.contains(AUTHORITY_USER)) {
                // 判断是普通用户，可能为作者

            }
        });*/
        return list;
    }

    /**
     * 授权用户为作者
     *
     * @param authority 权限
     * @return 权限集合
     */
    public Collection<? extends GrantedAuthority> getAuthorities(String authority) {
        // 获得数据库中的用户权限字段
        List<GrantedAuthority> list = new ArrayList<>();
        list.add((GrantedAuthority) () -> {
            // 获得用户的权限
            return authority;
        });
        return list;
    }
}
