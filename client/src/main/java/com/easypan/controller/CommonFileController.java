package com.easypan.controller;

import com.easypan.config.APPConfig;
import com.easypan.entity.constants.Constants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletResponse;

/**
 * @author: ZhangX
 * @createDate: 2023/7/24
 * @description:
 */
public class CommonFileController extends ABaseController {
    @Autowired
    private APPConfig appConfig;

    // 获取缩略图
    public void getImage(HttpServletResponse response, String imageFolder,
                         String imageName){
        if (com.easypan.utils.StringUtils.isEmpty(imageFolder) || StringUtils.isBlank(imageName)) {
            return;
        }
        String imageSuffix = com.easypan.utils.StringUtils.getFileSuffix(imageName);
        String filePath = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE
                + "/" + imageFolder + "/" + imageName;
        imageSuffix = imageSuffix.replace(".", "");
        String contentType = "image/" + imageSuffix;
        response.setContentType(contentType);
        response.setHeader("Cache-Control", "max-age=2592000");
        readFile(response, filePath);
    }
}
