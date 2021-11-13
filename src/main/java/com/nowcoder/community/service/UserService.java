package com.nowcoder.community.service;

import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ author : Real
 * @ date : 2021/11/9 15:21
 * @ description :
 */
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public User findUserById(int userId) {
        return userMapper.selectById(userId);
    }

    public String findUsername(int userId) {
        User user = userMapper.selectById(userId);
        return user.getUsername();
    }

}
