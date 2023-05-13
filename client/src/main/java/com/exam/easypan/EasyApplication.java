package com.exam.easypan;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author: ZhangX
 * @createDate: 2023/5/12
 * @description: pggdyjfmbsagbbef
 */

@SpringBootApplication
@EnableScheduling
@EnableAsync
@MapperScan("com.exam.easypan.mapper")
public class EasyApplication {
    public static void main(String[] args) {
        SpringApplication.run(EasyApplication.class,args);
    }
}
