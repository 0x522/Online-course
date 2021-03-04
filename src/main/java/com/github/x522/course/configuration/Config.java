package com.github.x522.course.configuration;


import com.github.x522.course.dao.SessionDao;
import com.github.x522.course.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.stream.Stream;


@Configuration
public class Config implements WebMvcConfigurer {
    @Autowired
    SessionDao sessionDao;
    private static final String COOKIE_NAME = "COURSE_APP_SESSION_ID";

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new UserInterceptor(sessionDao));
    }

    public static Optional<String> getCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return Optional.empty();
        }
        Cookie[] cookies = request.getCookies();

        return Stream.of(cookies)
                .filter(cookie -> cookie.getName().equals(COOKIE_NAME))
                .map(Cookie::getValue)
                .findFirst();
    }

    public static class UserContext {
        private static ThreadLocal<User> currentUser = new ThreadLocal<>();

        /**
         * @return 返回null代表没有登陆
         */
        public static User getCurrentUser() {
            return currentUser.get();
        }

        /**
         * 为当前线程上下文设置用户，null代表清空当前用户
         *
         * @param currentUser
         */
        public static void setCurrentUser(User currentUser) {
            UserContext.currentUser.set(currentUser);
        }
    }

}
