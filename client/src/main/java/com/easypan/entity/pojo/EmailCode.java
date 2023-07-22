package com.easypan.entity.pojo;

import com.easypan.enums.DateTimePatternEnum;
import com.easypan.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;


import java.io.Serializable;
import java.util.Date;


/**
 * @Author hapZhang
 * @Description 邮箱验证码
 * @Date 2023/07/21 21:37:32
 */
@Data
public class EmailCode implements Serializable {
    /**
     * 邮箱
     */
    private String email;

    /**
     * 验证码
     */
    private String code;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss")
    private Date createTime;

    /**
     * 状态
     * 0未使用1已使用
     */
    private Integer status;


}