package com.easypan.entity.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: ZhangX
 * @createDate: 2023/7/22
 * @description:
 */
@Data
public class UserSpaceDto implements Serializable {

    private Long useSpace;

    private Long totalSpace;

}