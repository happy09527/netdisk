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

    boolean checkLogin() default true;

    boolean checkAdmin() default false;
}
