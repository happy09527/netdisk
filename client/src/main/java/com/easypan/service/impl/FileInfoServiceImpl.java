package com.easypan.service.impl;


import cn.hutool.core.date.DateUtil;
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
import com.easypan.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
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

    @Resource
    @Lazy
    private FileInfoServiceImpl fileInfoService;

    @Override
    public FileInfo getFileInfoByFileIdAndUserId(String realFileId, String userId) {
        return fileInfoMapper.selectByFileIdAndUserId(realFileId, userId);
    }

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
     * @param fileId     文件id，非必传，第一个分片文件不传
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
        boolean uploadSuccess = true;
        File tempFileFolder = null;
        Date date = new Date();
        try {
            if (StringUtils.isEmpty(fileId)) {
                fileId = StringUtils.getRandomString(Constants.LENGTH_10);
            }
            resultDto.setFileId(fileId);
            // 获取用户可用空间
            UserSpaceDto spaceDto = redisComponent.getUserSpaceUse(userDto.getUserId());
            // 通过第一个文件序列来判断是否可以秒传
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
            tempFileFolder = new File(templateFileFolder + "/" + currentUserFolderName);
            if (!tempFileFolder.exists()) {
                tempFileFolder.mkdirs();
            }
            File newFile = new File(tempFileFolder.getPath() + "/" + chunkIndex);
            file.transferTo(newFile);
            redisComponent.saveFileTempSize(userDto.getUserId(), fileId, file.getSize());

            if (chunkIndex < chunks - 1) {
                resultDto.setStatus(UploadStatusEnums.UPLOADING.getCode());
                // 保存临时文件大小
                redisComponent.saveFileTempSize(userDto.getUserId(), fileId, file.getSize());
                return resultDto;
            }
            redisComponent.saveFileTempSize(userDto.getUserId(), fileId, file.getSize());
            //最后一个文件上传完成，保存数据库，异步合并文件
            String month = DateUtils.format(new Date(), DateTimePatternEnum.YYYYMM.getPattern());
            String fileSuffix = StringUtils.getFileSuffix(fileName);
            String realFileName = currentUserFolderName + fileSuffix;
            FileTypeEnums fileTypeEnum = FileTypeEnums.getFileTypeBySuffix(fileSuffix);
            //自动重命名
            fileName = autoRename(filePid, userDto.getUserId(), fileName);
            FileInfo fileInfo = new FileInfo();
            fileInfo.setFileId(fileId);
            fileInfo.setUserId(userDto.getUserId());
            fileInfo.setFileMd5(fileMd5);
            fileInfo.setFileName(fileName);
            fileInfo.setFilePath(month + "/" + realFileName);
            fileInfo.setFilePid(filePid);
            fileInfo.setCreateTime(date);
            fileInfo.setLastUpdateTime(date);
            fileInfo.setFileCategory(fileTypeEnum.getCategory().getCategory());
            fileInfo.setFileType(fileTypeEnum.getType());
            fileInfo.setStatus(FileStatusEnums.TRANSFER.getStatus());
            fileInfo.setFolderType(FileFolderTypeEnums.FILE.getType());
            fileInfo.setDelFlag(FileDelFlagEnums.USING.getFlag());
            this.fileInfoMapper.insert(fileInfo);

            Long totalSize = redisComponent.getFileTempSize(userDto.getUserId(), fileId);
            updateUseSpace(userDto, totalSize);
            resultDto.setStatus(UploadStatusEnums.UPLOAD_FINISH.getCode());
            // 事务提交后执行
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    fileInfoService.transferFile(fileInfo.getFileId(), userDto);
                    redisComponent.removeFileTempSize(userDto.getUserId(), fileInfo.getFileId());
                }
            });
        } catch (BusinessException e) {
            log.error("文件上传失败");
            uploadSuccess = false;
            throw e;
        } catch (IOException e) {
            log.error("文件上传失败");
            uploadSuccess = false;
        } finally {
            if (tempFileFolder != null && !uploadSuccess) {
                try {
                    FileUtils.deleteDirectory(tempFileFolder);
                } catch (Exception e) {
                    log.error("删除临时目录失败");
                }
            }
        }
        return resultDto;
    }

    /**
     * @date: 2023/8/1 17:37
     * 文件转码
     */
    @Async
    public void transferFile(String fileId, SessionWebUserDto userDto) {
        boolean transferSuccess = true;
        String targetFilePath = null;
        String cover = null;
        FileTypeEnums fileTypeEnum = null;
        FileInfo fileInfo = fileInfoMapper.selectByFileIdAndUserId(fileId, userDto.getUserId());
        try {
            if (fileInfo == null || !FileStatusEnums.TRANSFER.getStatus().equals(fileInfo.getStatus())) {
                return;
            }
            String tempFolderName = appConfig.getProjectFolder() + Constants.FILE_FOLDER_TEMP;
            String currentUserFolderName = userDto.getUserId() + fileId;
            File fileFolder = new File(tempFolderName + "/" + currentUserFolderName);
            if (!fileFolder.exists()) {
                fileFolder.mkdirs();
            }
            //文件后缀
            String fileSuffix = StringUtils.getFileSuffix(fileInfo.getFileName());
            String month = DateUtil.format(fileInfo.getCreateTime(), DateTimePatternEnum.YYYYMM.getPattern());
            // 目标目录 E:/javaPro/netdisk/ + /file + /{month}
            String targetFolderName = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE;
            File targetFolder = new File(targetFolderName + "/" + month);
            if (!targetFolder.exists()) {
                targetFolder.mkdirs();
            }
            //真实文件名 {userId+fileId}
            String realFileName = currentUserFolderName + fileSuffix;
            //真实文件路径
            targetFilePath = targetFolder.getPath() + "/" + realFileName;
            //合并文件
            /**
             * fileFolder.getPath() 临时目录
             * targetFilePath 目标目录
             * fileInfo.getFileName() 文件名
             * delSource
             */
            union(fileFolder.getPath(), targetFilePath, fileInfo.getFileName(), true);
            fileTypeEnum = FileTypeEnums.getFileTypeBySuffix(fileSuffix);
            if (FileTypeEnums.VIDEO.equals(fileTypeEnum)) {
                //视频文件切割
                cutFile4Video(fileId, targetFilePath);
                //视频生成缩略图
                cover = month + "/" + currentUserFolderName + Constants.IMAGE_PNG_SUFFIX;
                String coverPath = targetFolderName + "/" + cover;
                ScaleFilter.createCover4Video(new File(targetFilePath), Constants.LENGTH_150, new File(coverPath));
            } else if (FileTypeEnums.IMAGE == fileTypeEnum) {
                //生成缩略图
                cover = month + "/" + realFileName.replace(".", "_.");
                String coverPath = targetFolderName + "/" + cover;
                Boolean created = ScaleFilter.createThumbnailWidthFFmpeg(new File(targetFilePath), Constants.LENGTH_150, new File(coverPath), false);
                // 如果没有生成缩略图，直接将原图复制当做缩略图
                if (!created) {
                    FileUtils.copyFile(new File(targetFilePath), new File(coverPath));
                }
            }
        } catch (Exception e) {
            log.error("文件转码失败，文件Id:{},userId:{}", fileId, userDto.getUserId(), e);
            transferSuccess = false;
        } finally {
            FileInfo updateInfo = new FileInfo();
            updateInfo.setFileSize(new File(targetFilePath).length());
            updateInfo.setFileCover(cover);
            updateInfo.setStatus(transferSuccess ? FileStatusEnums.USING.getStatus() : FileStatusEnums.TRANSFER_FAIL.getStatus());
            fileInfoMapper.updateFileStatusWithOldStatus(fileId, userDto.getUserId(), updateInfo, FileStatusEnums.TRANSFER.getStatus());
        }
    }

    // 利用java代码操作命令行窗口执行FFmpeg对视频进行切割，生成.m3u8索引文件和.ts切片文件
    private void cutFile4Video(String fileId, String videoFilePath) {
        // 创建同名切片目录
        File tsFolder = new File(videoFilePath.substring(0, videoFilePath.lastIndexOf('.')));
        if (!tsFolder.exists()) {
            tsFolder.mkdirs();
        }
        final String CMD_TRANSFER_2TS = "ffmpeg -y -i %s  -vcodec copy -acodec copy -vbsf h264_mp4toannexb %s";
        final String CMD_CUT_TS = "ffmpeg -i %s -c copy -map 0 -f segment -segment_list %s -segment_time 30 %s/%s_%%4d.ts";
        String tsPath = tsFolder + "/" + Constants.TS_NAME;
        // 生成.ts
        String cmd = String.format(CMD_TRANSFER_2TS, videoFilePath, tsPath);
        ProcessUtils.executeCommand(cmd, false);
        // 生成索引文件.m3u8 和切片.ts 文件
        cmd = String.format(CMD_CUT_TS, tsPath, tsFolder.getPath() + "/" + Constants.M3U8_NAME, tsFolder.getPath(), fileId);
        ProcessUtils.executeCommand(cmd, false);
        // 删除 index.ts文件
        new File(tsPath).delete();
    }

    /**
     * @date: 2023/8/1 17:31
     * 合并文件，完成后删除
     * @param: 临时目录、目标目录、文件名、是否删除
     */
    public static void union(String dirPath, String toFilePath, String fileName, boolean delSource)
            throws BusinessException {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            throw new BusinessException("目录不存在");
        }
        File[] files = dir.listFiles();
        File targetFile = new File(toFilePath);
        RandomAccessFile writeFile = null;
        try {
            writeFile = new RandomAccessFile(targetFile, "rw");
            byte[] b = new byte[1024 * 10];
            for (int i = 0; i < files.length; i++) {
                int len;
                File chunkFile = new File(dirPath + "/" + i);
                RandomAccessFile readFile = null;
                try {
                    readFile = new RandomAccessFile(chunkFile, "r");
                    while ((len = readFile.read(b)) != -1) {
                        writeFile.write(b, 0, len);
                    }
                } catch (Exception e) {
                    logger.error("合并分片失败", e);
                    throw new BusinessException("合并分片失败");
                } finally {
                    if (readFile != null) {
                        readFile.close();
                    }
                }
            }
        } catch (Exception e) {
            logger.error("合并文件：{}失败", fileName, e);
            throw new BusinessException("合并文件" + fileName + "错误");
        } finally {
            try {
                if (writeFile != null) {
                    writeFile.close();
                }
            } catch (IOException e) {
                log.error("关闭流失败", e);
            }
            if (delSource) {
                if (dir.exists()) {
                    try {
                        // 以递归方式删除目录。
                        FileUtils.deleteDirectory(dir);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
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