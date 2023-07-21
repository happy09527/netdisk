package com.easypan.entity.pojo;

import com.easypan.enums.DateTimePatternEnum;
import com.easypan.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;


import java.io.Serializable;
import java.util.Date;


/**
 * @Author hapZhang
 * @Description 邮箱验证码
 * @Date 2023/07/21 21:37:32
 */
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
	@JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss",timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss")
	private Date createTime;

	/**
	 * 状态
0未使用1已使用
	 */
	private Integer status;

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmail() {
		return this.email;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCode() {
		return this.code;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getStatus() {
		return this.status;
	}

	@Override
	public String toString() {
		return "邮箱:" + (email == null ? "空" : email) + ",验证码:" + (code == null ? "空" : code) + ",创建时间:" + (createTime == null ? "空" : DateUtils.format(createTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern())) + ",状态 0未使用1已使用:" + (status == null ? "空" : status);
	}
}