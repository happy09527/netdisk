package com.easypan.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author: ZhangX
 * @createDate: 2023/7/22
 * @description: 读取properties文件中的管理员信息
 */
@Component("appConfig")
public class APPConfig {


    @Value("${spring.mail.username:}")
    private String sendUserName;


    @Value("${admin.emails:}")
    private String adminEmails;

    @Value("${project.folder:}")
    private String projectFolder;

    public String getSendUserName() {
        return sendUserName;
    }

    public String getAdminEmails() {
        return adminEmails;
    }
    public String getProjectFolder() {
        return projectFolder;
    }
}
