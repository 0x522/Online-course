package com.github.x522.course.annotation;

import com.github.x522.course.dao.UserDao;
import com.github.x522.course.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 管理用户的角色，要求当前登录的用户必须是管理员
 */
@Service
public class UserRoleManagerService {
    @Autowired
    UserDao userDao;

    @Admin
    public List<User> getAllUsers() {
        return userDao.findAll();
    }
}
