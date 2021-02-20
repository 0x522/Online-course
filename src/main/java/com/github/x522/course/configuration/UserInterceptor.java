package com.github.x522.course.configuration;

import com.github.x522.course.dao.SessionDao;
import com.github.x522.course.model.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.stream.Stream;

public class UserInterceptor implements HandlerInterceptor {
    public static String COOKIE_NAME = "COURSE_APP_SESSION_ID";

    @Autowired
    SessionDao sessionDao;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Cookie[] cookies = request.getCookies();
        Stream.of(cookies)
                .filter(cookie -> COOKIE_NAME.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .flatMap(cookieValue -> sessionDao.findByCookie(cookieValue))
                .map(Session::getUser)
                .ifPresent(Config.UserContext::setCurrentUser);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        Config.UserContext.setCurrentUser(null);
    }
}
