package com.easypan.service;

import com.easypan.entity.vo.PaginationResultVo;
import com.easypan.entity.pojo.EmailCode;
import com.easypan.entity.query.EmailCodeQuery;

import java.util.List;
/**
 * @Author hapZhang
 * @Description 邮箱验证码Service
 * @Date 2023/07/21 21:37:32
 */
public interface EmailCodeService{

    void sendEmailCode(String email, Integer type);

    void checkCode(String email, String emailCode);

}