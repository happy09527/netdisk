package com.easypan.annotation;

import org.springframework.web.bind.annotation.Mapping;

import java.lang.annotation.*;

/**
 * @author: ZhangX
 * @createDate: 2023/7/22
 * @description:
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Mapping
public @interface GlobalInterceptor {
    /**
     * @date: 2023/7/22 13:42
     * 参数校验
     **/
    boolean checkParams() default false;
    /**
     * @date: 2023/7/24 15:57
     * 登录校验
     **/
    boolean checkLogin() default true;
    /**
     * @date: 2023/7/24 15:57
     * 管理员校验
     **/
    boolean checkAdmin() default false;
}
