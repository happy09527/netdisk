package com.easypan.controller;

import com.easypan.entity.constants.Constants;
import com.easypan.entity.dto.SessionWebUserDto;
import com.easypan.entity.enums.ResponseCodeEnum;;
import com.easypan.entity.vo.PaginationResultVo;
import com.easypan.entity.vo.ResponseVo;
import com.easypan.utils.CopyUtils;
import com.easypan.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;;import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ABaseController {
    private static final Logger logger = LoggerFactory.getLogger(ABaseController.class);
    protected static final String STATUS_SUCCESS = "success";
    protected static final String STATUS_ERROR = "error";

    protected <T> ResponseVo getSuccessResponseVo(T t) {
        ResponseVo<T> responseVo = new ResponseVo<>();
        responseVo.setStatus(STATUS_SUCCESS);
        responseVo.setCode(ResponseCodeEnum.CODE_200.getCode());
        responseVo.setInfo(ResponseCodeEnum.CODE_200.getMsg());
        responseVo.setData(t);
        return responseVo;
    }

    /**
     * @date: 2023/7/23 11:10
     * 将文件写入HttpServletResponse
     **/
    protected void readFile(HttpServletResponse response, String filePath) {
        if (!StringUtils.pathIsOk(filePath)) {
            return;
        }
        OutputStream out = null;
        FileInputStream in = null;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                return;
            }
            in = new FileInputStream(file);
            byte[] byteData = new byte[1024];
            out = response.getOutputStream();
            int len = 0;
            while ((len = in.read(byteData)) != -1) {
                out.write(byteData, 0, len);
            }
            out.flush();
        } catch (Exception e) {
            logger.error("读取文件异常", e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    logger.error("IO异常", e);
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    logger.error("IO异常", e);
                }
            }
        }
    }

    /**
     * @date: 2023/7/23 20:20
     * 从httpSession中获取当前用户信息
     **/
    public SessionWebUserDto getUserInfoFromSession(HttpSession session) {
        SessionWebUserDto sessionAttribute = (SessionWebUserDto) session.getAttribute(Constants.SESSION_KEY);
        return sessionAttribute;
    }

    /**
     * @date: 2023/7/24 22:02
     * 将对象几个属性去除
     **/
    protected <S, T> PaginationResultVo<T> convert2PaginationVO(PaginationResultVo<S> result, Class<T> clazz) {
        PaginationResultVo<T> resultVO = new PaginationResultVo<>();
        resultVO.setList(CopyUtils.copyList(result.getList(), clazz));
        resultVO.setPageNo(result.getPageNo());
        resultVO.setPageSize(result.getPageSize());
        resultVO.setPageTotal(result.getPageTotal());
        resultVO.setTotalCount(result.getTotalCount());
        return resultVO;
    }
}