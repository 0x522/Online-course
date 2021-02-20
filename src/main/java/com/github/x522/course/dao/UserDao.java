package com.github.x522.course.dao;

import com.github.x522.course.model.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao {
    User findUsersByUsername(String username);

    void save(User user);
}
