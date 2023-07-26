package com.easypan.service.impl;


import com.easypan.component.RedisComponent;
import com.easypan.config.APPConfig;
import com.easypan.entity.constants.Constants;
import com.easypan.entity.dto.SessionWebUserDto;
import com.easypan.entity.dto.UploadResultDto;
import com.easypan.entity.dto.UserSpaceDto;
import com.easypan.entity.enums.*;
import com.easypan.entity.pojo.FileInfo;
import com.easypan.entity.pojo.UserInfo;
import com.easypan.entity.query.FileInfoQuery;
import com.easypan.entity.query.SimplePage;
import com.easypan.entity.query.UserInfoQuery;
import com.easypan.entity.vo.PaginationResultVo;
import com.easypan.exception.BusinessException;
import com.easypan.mappers.FileInfoMapper;
import com.easypan.mappers.UserInfoMapper;
import com.easypan.service.FileInfoService;
import com.easypan.service.UserInfoService;
import com.easypan.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.*;


/**
 * 文件信息 业务接口实现
 */
@Service
@Slf4j
public class FileInfoServiceImpl implements FileInfoService {
    private static final Logger logger = LoggerFactory.getLogger(FileInfoServiceImpl.class);
    @Resource
    private FileInfoMapper<FileInfo, FileInfoQuery> fileInfoMapper;

    @Resource
    private RedisComponent redisComponent;

    @Resource
    private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;

    @Resource
    private APPConfig appConfig;

    /**
     * 分页查询方法
     */
    @Override
    public PaginationResultVo<FileInfo> findListByPage(FileInfoQuery param) {
        // 记录条数
        int count = fileInfoMapper.selectCount(param);
        // 如果未选择页面大小则使用默认的15
        int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();
        /**
         * param.getPageNo() 第几页
         * count 共多少条记录
         * pageSize 一页显示多少页
         */
        SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
        param.setSimplePage(page);
        List<FileInfo> list = fileInfoMapper.selectList(param);
        PaginationResultVo<FileInfo> result = new PaginationResultVo(count,
                page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
        return result;
    }

    /**
     * @param userDto    用户信息
     * @param fileId     非必传，第一个分片文件不传
     * @param file       传的文件
     * @param fileName   文件名
     * @param filePid    在哪一个目录
     * @param fileMd5    前端做的
     * @param chunkIndex 第几个分片
     * @param chunks     总共有多少个分片
     * @date: 2023/7/24 22:43
     * 上传文件
     **/
    @Override
    @Transactional
    public UploadResultDto uploadFile(SessionWebUserDto userDto, MultipartFile file, String fileId,
                                      String filePid, String fileName, String fileMd5, Integer chunkIndex, Integer chunks) {
        UploadResultDto resultDto = new UploadResultDto();
        try {
            if (StringUtils.isEmpty(fileId)) {
                fileId = StringUtils.getRandomNumber(Constants.LENGTH_10);
            }
            resultDto.setFileId(fileId);
            Date date = new Date();
            UserSpaceDto spaceDto = redisComponent.getUserSpaceUse(userDto.getUserId());
            if (chunkIndex == 0) {
                FileInfoQuery fileInfoQuery = new FileInfoQuery();
                fileInfoQuery.setFileMd5(fileMd5);
                fileInfoQuery.setSimplePage(new SimplePage(0, 1));
                fileInfoQuery.setStatus(FileStatusEnums.USING.getStatus());
                List<FileInfo> dbFileList = this.fileInfoMapper.selectList(fileInfoQuery);
                // 秒传
                if (!dbFileList.isEmpty()) {
                    FileInfo fileInfo = dbFileList.get(0);
                    // 判断文件大小
                    if (fileInfo.getFileSize() + spaceDto.getUseSpace() > spaceDto.getTotalSpace()) {
                        throw new BusinessException(ResponseCodeEnum.CODE_904);
                    }
                    fileInfo.setFileId(fileId);
                    fileInfo.setFilePid(filePid);
                    fileInfo.setUserId(userDto.getUserId());
                    fileInfo.setCreateTime(date);
                    fileInfo.setLastUpdateTime(date);
                    fileInfo.setStatus(FileStatusEnums.USING.getStatus());
                    fileInfo.setDelFlag(FileDelFlagEnums.USING.getFlag());
                    fileInfo.setFileMd5(fileMd5);
                    // 文件重命名
                    fileName = autoRename(filePid, userDto.getUserId(), fileName);
                    fileInfo.setFileName(fileName);
                    fileInfoMapper.insert(fileInfo);
                    resultDto.setStatus(UploadStatusEnums.UPLOAD_SECONDS.getCode());
                    // 更新用户使用空间大小
                    updateUseSpace(userDto, fileInfo.getFileSize());
                    return resultDto;
                }
            }
            // 判断临时文件大小
            Long currentFileSize = redisComponent.getFileTempSize(userDto.getUserId(), fileId);
            if (currentFileSize + file.getSize() + spaceDto.getUseSpace() > spaceDto.getTotalSpace()) {
                throw new BusinessException(ResponseCodeEnum.CODE_904);
            }
            // 暂存临时目录
            String templateFileFolder = appConfig.getProjectFolder() + Constants.FILE_FOLDER_TEMP;
            String currentUserFolderName = userDto.getUserId() + fileId;
            File tempFileFolder = new File(templateFileFolder + currentUserFolderName);
            if (!tempFileFolder.exists()) {
                tempFileFolder.mkdirs();
            }
            File newFile = new File(tempFileFolder + "/" + chunkIndex);
            file.transferTo(newFile);
            if (chunkIndex < chunks - 1) {
                resultDto.setStatus(UploadStatusEnums.UPLOADING.getCode());
                // 保存临时文件大小
                redisComponent.saveFileTempSize(userDto.getUserId(), fileId, file.getSize());
                return resultDto;
            }
        } catch (IOException e) {
            logger.error("文件上传失败", e);
        }

        return resultDto;
    }

    /**
     * @date: 2023/7/25 11:06
     * 更新用户可用空间大小.需要同步更新redis内的信息
     **/
    private void updateUseSpace(SessionWebUserDto webUserDto, Long useSpace) {
        Integer count = userInfoMapper.updateUseSpace(webUserDto.getUserId(), useSpace, null);
        if (count == 0) {
            throw new BusinessException(ResponseCodeEnum.CODE_904);
        }
        UserSpaceDto spaceDto = redisComponent.getUserSpaceUse(webUserDto.getUserId());
        spaceDto.setUseSpace(spaceDto.getUseSpace() + useSpace);
        redisComponent.saveUserSpaceUse(webUserDto.getUserId(), spaceDto);
    }

    /**
     * @date: 2023/7/25 10:34
     * 重命名文件
     **/
    private String autoRename(String filePid, String userId, String fileName) {
        // 封装查询条件，如果查询出数据，则需要重命名
        FileInfoQuery fileInfoQuery = new FileInfoQuery();
        fileInfoQuery.setUserId(userId);
        fileInfoQuery.setFilePid(filePid);
        fileInfoQuery.setDelFlag(FileDelFlagEnums.USING.getFlag());
        fileInfoQuery.setFileName(fileName);
        Integer count = this.fileInfoMapper.selectCount(fileInfoQuery);
        if (count > 0) {
            return StringUtils.rename(fileName);
        }
        return fileName;
    }

    /**
     * @date: 2023/7/25 8:57
     * 查看用户已使用空间大小
     **/
    @Override
    public Long selectUseSpace(String userId) {
        return this.fileInfoMapper.selectUseSpace(userId);
    }
}