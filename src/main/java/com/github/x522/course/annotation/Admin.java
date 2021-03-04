package com.github.x522.course.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 标记一个service方法，只有拥有管理员权限的用户才能访问该方法，其他用户会直接失败
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Admin {
}
