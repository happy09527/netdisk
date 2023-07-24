package com.easypan.annotation;

import com.easypan.entity.enums.VerifyRegexEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author: ZhangX
 * @createDate: 2023/7/22
 * @description:
 */
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface VerifyParam {
    int min() default -1;

    int max() default -1;

    boolean required() default false;

    //默认不校验
    VerifyRegexEnum regex() default VerifyRegexEnum.NO;

}
