package com.github.x522.course.dao;

import com.github.x522.course.model.Session;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;


public interface SessionDao extends CrudRepository<Session, Integer> {
    Optional<Session> findByCookie(String cookie);

    void deleteByCookie(String cookie);
}
