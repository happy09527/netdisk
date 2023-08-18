package com.easypan.controller;


import com.easypan.annotation.GlobalInterceptor;
import com.easypan.annotation.VerifyParam;
import com.easypan.entity.dto.SessionWebUserDto;
import com.easypan.entity.dto.UploadResultDto;
import com.easypan.entity.enums.FileCategoryEnums;
import com.easypan.entity.enums.FileDelFlagEnums;
import com.easypan.entity.query.FileInfoQuery;
import com.easypan.entity.vo.FileInfoVO;
import com.easypan.entity.vo.PaginationResultVo;
import com.easypan.entity.vo.ResponseVo;
import com.easypan.service.FileInfoService;
import com.easypan.utils.CopyUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.List;

/**
 * 文件信息 Controller
 */
@RestController
@RequestMapping("/file")
public class FileInfoController extends CommonFileController {

    @Resource
    private FileInfoService fileInfoService;

    /**
     * @date: 2023/7/24 21:36
     * 根据条件分页查询
     **/
    @RequestMapping("/loadDataList")
    @GlobalInterceptor(checkParams = true, checkLogin = true)
    public ResponseVo loadDataList(HttpSession session, FileInfoQuery query, String category) {
        FileCategoryEnums fileCategoryEnums = FileCategoryEnums.getByCode(category);
        if (fileCategoryEnums != null) {
            query.setFileCategory(fileCategoryEnums.getCategory());
        }
        query.setUserId(getUserInfoFromSession(session).getUserId());
        query.setDelFlag(FileDelFlagEnums.USING.getFlag());
        query.setOrderBy("last_update_time desc");
        PaginationResultVo resultVo = fileInfoService.findListByPage(query);
        return getSuccessResponseVo(convert2PaginationVO(resultVo, FileInfoVO.class));
    }

    /**
     * @date: 2023/7/25 9:33
     * 上传文件服务
     **/
    @RequestMapping("/uploadFile")
    @GlobalInterceptor(checkParams = true, checkLogin = true)
    public ResponseVo uploadFile(HttpSession session,
                                 String fileId,
                                 MultipartFile file,
                                 @VerifyParam(required = true) String filePid,
                                 @VerifyParam(required = true) String fileName,
                                 @VerifyParam(required = true) String fileMd5,
                                 @VerifyParam(required = true) Integer chunkIndex,
                                 @VerifyParam(required = true) Integer chunks) {
        SessionWebUserDto userDto = getUserInfoFromSession(session);
        UploadResultDto uploadResultDto = fileInfoService.uploadFile(userDto, file, fileId, filePid, fileName, fileMd5, chunkIndex, chunks);
        return getSuccessResponseVo(uploadResultDto);
    }
    // 获取缩略图
    @GetMapping("/getImage/{imageFolder}/{imageName}")
    @GlobalInterceptor(checkParams = true)
    public void getImage(HttpServletResponse response, @PathVariable(name = "imageFolder") String imageFolder,
                         @PathVariable(name = "imageName") String imageName) {
        super.getImage(response, imageFolder, imageName);
    }

    // 获取视频信息（时长）、视频分片
    @GetMapping("/ts/getVideoInfo/{fileId}")
    @GlobalInterceptor(checkParams = true)
    public void getVideoInfo(HttpServletResponse response,
                             HttpSession session,
                             @PathVariable("fileId") @VerifyParam(required = true) String fileId) {
        SessionWebUserDto webUserDto = getUserInfoFromSession(session);
        super.getFile(response, fileId, webUserDto.getUserId());
    }

    // 获取文件信息。非视频文件，不用进入文件夹，专用接口
    @RequestMapping("/getFile/{fileId}")
    public void getFile(HttpServletResponse response,
                        HttpSession session,
                        @PathVariable("fileId") @VerifyParam(required = true) String fileId) {
        SessionWebUserDto webUserDto = getUserInfoFromSession(session);
        super.getFile(response, fileId, webUserDto.getUserId());
    }

}