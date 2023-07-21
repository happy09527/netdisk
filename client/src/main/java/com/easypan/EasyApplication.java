package com.easypan;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author: ZhangX
 * @createDate: 2023/7/21
 * @description:
 */

@EnableAsync
@SpringBootApplication(scanBasePackages = {"com.easypan"})
@EnableTransactionManagement
@EnableScheduling
@MapperScan(basePackages = {"com.easypan.mappers"})
public class EasyApplication {
    public static void main(String[] args) {
        SpringApplication.run(EasyApplication.class,args);
    }
}
