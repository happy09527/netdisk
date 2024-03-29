package com.easypan.service;

import com.easypan.entity.dto.SessionWebUserDto;
import com.easypan.entity.dto.UploadResultDto;
import com.easypan.entity.pojo.FileInfo;
import com.easypan.entity.query.FileInfoQuery;
import com.easypan.entity.vo.PaginationResultVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


/**
 * 文件信息 业务接口
 */
public interface FileInfoService {

    FileInfo getFileInfoByFileIdAndUserId(String realFileId, String userId);

    PaginationResultVo<FileInfo> findListByPage(FileInfoQuery param);


    UploadResultDto uploadFile(SessionWebUserDto userDto, MultipartFile file, String fileId, String filePid, String fileName, String fileMd5, Integer chunkIndex, Integer chunks);

    Long selectUseSpace(String userId);

    FileInfo newFolder(String filePid, String userId, String fileName);

    List<FileInfo> findListByParam(FileInfoQuery infoQuery);

    FileInfo rename(String fileName, String fileId, String userId);

    List<FileInfo> loadAllFolder(String userId, String filePid, String currentFileIds);

    void changeFileFolder(String fileIds, String filePid, String userId);

    void removeFile2RecycleBatch(String userId, String fileIds);

    void recoverFile(String fileIds, String userId);

    void deleteFile(String fileIds, String userId, boolean adminOp);
}