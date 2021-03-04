package com.github.x522.course.aop;

import com.github.x522.course.configuration.Config;
import com.github.x522.course.model.HttpException;
import com.github.x522.course.model.User;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;

@Configuration
@Aspect
public class AdminAOPManager {
    @Around("@annotation(com.github.x522.course.annotation.Admin)")
    Object checkPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        User currentUser = Config.UserContext.getCurrentUser();
        if (currentUser == null) {
            throw new HttpException(403, "没有权限!");
        } else if (currentUser.getRoles().stream().anyMatch(role -> "管理员".equals(role.getName()))) {
            return joinPoint.proceed();
        } else {
            throw new HttpException(403, "没有权限!");
        }
    }
}
