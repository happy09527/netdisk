package com.easypan.controller;

import com.easypan.enums.ResponseCodeEnum;;
import com.easypan.entity.vo.ResponseVo;;
public class ABaseController {
    protected static final String STATUS_SUCCESS = "success";
    protected static final String STATUS_ERROR = "error";

    protected <T> ResponseVo getSuccessResponseVo(T t){
        ResponseVo<T> responseVo = new ResponseVo<>();
        responseVo.setStatus(STATUS_SUCCESS);
        responseVo.setCode(ResponseCodeEnum.CODE_200.getCode());
        responseVo.setInfo(ResponseCodeEnum.CODE_200.getMsg());
        responseVo.setData(t);
        return responseVo;
    }
}