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



    PaginationResultVo<FileInfo> findListByPage(FileInfoQuery param);


    UploadResultDto uploadFile(SessionWebUserDto userDto, MultipartFile file, String fileId, FileInfoQuery fileInfoQuery, String filePid, String fileName, String fileMd5, Integer chunkIndex, Integer chunks);
}