package com.nowcoder.community.util;

import com.nowcoder.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * @ author : Real
 * @ date : 2021/11/20 20:50
 * @ description : 保存来自不同线程的用户，用于代替 Session 对象
 */
@Component
public class HostHolder {

    private final ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user) {
        users.set(user);
    }

    public User getUser() {
        return users.get();
    }

    public void removeUser() {
        users.remove();
    }

}
