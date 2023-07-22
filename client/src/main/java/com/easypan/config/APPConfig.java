package com.easypan.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author: ZhangX
 * @createDate: 2023/7/22
 * @description:
 */
@Component("appConfig")
public class APPConfig {
    @Value("${spring.mail.username}")
    private String sendUserName;


    @Value("${admin.emails:}")
    private String adminEmails;

    public String getSendUserName() {
        return sendUserName;
    }
}
