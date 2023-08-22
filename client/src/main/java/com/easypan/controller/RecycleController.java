package com.easypan.controller;

import com.easypan.annotation.GlobalInterceptor;
import com.easypan.annotation.VerifyParam;
import com.easypan.entity.dto.SessionWebUserDto;
import com.easypan.entity.enums.FileDelFlagEnums;
import com.easypan.entity.query.FileInfoQuery;
import com.easypan.entity.vo.FileInfoVO;
import com.easypan.entity.vo.PaginationResultVo;
import com.easypan.entity.vo.ResponseVo;
import com.easypan.service.FileInfoService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * @author: ZhangX
 * @createDate: 2023/8/20
 * @description:
 */
@RestController
@RequestMapping("/recycle")
public class RecycleController extends CommonFileController{
    @Resource
    private FileInfoService fileInfoService;

    // 加载回收站列表
    @PostMapping("/loadRecycleList")
    @GlobalInterceptor(checkParams = true)
    public ResponseVo loadRecycleList(HttpSession session, Integer pageNo, Integer pageSize) {
        FileInfoQuery query = new FileInfoQuery();
        query.setPageSize(pageSize);
        query.setPageNo(pageNo);
        query.setUserId(getUserInfoFromSession(session).getUserId());
        query.setOrderBy("recovery_time desc");
        query.setDelFlag(FileDelFlagEnums.RECYCLE.getFlag());
        PaginationResultVo result = fileInfoService.findListByPage(query);
        return getSuccessResponseVo(convert2PaginationVO(result, FileInfoVO.class));
    }
    // 恢复回收站文件
    @PostMapping("/recoverFile")
    @GlobalInterceptor(checkParams = true)
    public ResponseVo recoverFile(HttpSession session, @VerifyParam(required = true) String fileIds) {
        SessionWebUserDto userDto = getUserInfoFromSession(session);
        fileInfoService.recoverFile(fileIds,userDto.getUserId());
        return getSuccessResponseVo(null);
    }
    // 删除回收站文件
    @PostMapping("/deleteFile")
    @GlobalInterceptor(checkParams = true)
    public ResponseVo deleteFile(HttpSession session, @VerifyParam(required = true) String fileIds) {
        SessionWebUserDto userDto = getUserInfoFromSession(session);
        fileInfoService.deleteFile(fileIds,userDto.getUserId(),false);
        return getSuccessResponseVo(null);
    }
}
