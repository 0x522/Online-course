package com.github.x522.course.dao;

import com.github.x522.course.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Integer> {

}
