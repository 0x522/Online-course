package com.github.x522.course.configuration;


import com.github.x522.course.model.User;
import org.springframework.context.annotation.Configuration;


@Configuration
public class Config {
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
