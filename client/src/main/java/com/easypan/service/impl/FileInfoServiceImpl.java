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
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * 文件信息 业务接口实现
 */
@Service
@Slf4j
public class FileInfoServiceImpl implements FileInfoService {

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
     * @date: 2023/7/24 22:43
     * 上传文件
     **/
    @Override
    public UploadResultDto uploadFile(SessionWebUserDto userDto, MultipartFile file, String fileId, FileInfoQuery fileInfoQuery, String filePid, String fileName, String fileMd5, Integer chunkIndex, Integer chunks) {
        return null;
    }
}