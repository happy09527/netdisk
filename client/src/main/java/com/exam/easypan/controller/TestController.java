package com.exam.easypan.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: ZhangX
 * @createDate: 2023/5/12
 * @description:
 */
@RestController
public class TestController {
    @RequestMapping("/test")
    public String test(){
        return ":asd";
    }
}
