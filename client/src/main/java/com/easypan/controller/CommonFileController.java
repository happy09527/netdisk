package com.easypan.controller;

import com.easypan.config.APPConfig;
import com.easypan.entity.constants.Constants;
import com.easypan.entity.enums.FileCategoryEnums;
import com.easypan.entity.pojo.FileInfo;
import com.easypan.service.FileInfoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletResponse;
import java.io.File;

/**
 * @author: ZhangX
 * @createDate: 2023/7/24
 * @description:
 */
public class CommonFileController extends ABaseController {
    @Autowired
    private APPConfig appConfig;

    @Autowired
    private FileInfoService fileInfoService;

    // 获取缩略图
    public void getImage(HttpServletResponse response, String imageFolder,
                         String imageName) {
        if (StringUtils.isEmpty(imageFolder) || StringUtils.isBlank(imageName)) {
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

    // 处理第一次获取.m3u8索引文件和后续获取.ts分片文件
    // 同时也处理其他文件
    protected void getFile(HttpServletResponse response, String fileId, String userId) {
        String filePath = null;
        if(fileId.endsWith(".ts")){
            // 如果是后去ts分片文件
            String[] tsArray = fileId.split("_");
            String realFileId = tsArray[0];
            FileInfo fileInfo = fileInfoService.getFileInfoByFileIdAndUserId(realFileId, userId);
            if (fileInfo == null) {
                return;
            }
            String fileName = fileInfo.getFilePath();
            fileName = com.easypan.utils.StringUtils.getFileNameNoSuffix(fileName) + "/" + fileId;
            filePath = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE + "/" + fileName;
        }else{
            // 第一次获取.m3u8索引文件
            FileInfo fileInfo = fileInfoService.getFileInfoByFileIdAndUserId(fileId, userId);
            if (fileInfo == null) {
                return;
            }
            //视频文件读取.m3u8文件
            if(FileCategoryEnums.VIDEO.getCategory().equals(fileInfo.getFileCategory())){
                String fileNameNoSuffix = com.easypan.utils.StringUtils.getFileNameNoSuffix(fileInfo.getFilePath());
                filePath = appConfig.getProjectFolder() +Constants.FILE_FOLDER_FILE+"/"+ fileNameNoSuffix + "/" + Constants.M3U8_NAME;
            }else{
                // 处理其他文件
                filePath = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE
                        + "/" + fileInfo.getFilePath();
            }
        }

        File file = new File(filePath);
        if(!file.exists()){
            return;
        }
        readFile(response,filePath);
    }
}
