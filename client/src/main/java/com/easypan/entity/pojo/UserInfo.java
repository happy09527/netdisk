package com.easypan.entity.pojo;

import com.easypan.entity.enums.DateTimePatternEnum;
import com.easypan.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;


import java.io.Serializable;
import java.util.Date;


/**
 * @Author hapZhang
 * @Description 用户信息表
存储用户登录信息、云盘空间大小
 * @Date 2023/07/21 14:42:24
 */
public class UserInfo implements Serializable {
	/**
	 * 用户id
	 */
	private String userId;

	/**
	 * 昵称
	 */
	private String nickname;

	/**
	 * 邮箱
	 */
	private String email;

	/**
	 * qqOpenId
	 */
	@JsonIgnore
	private String qqOpenId;

	/**
	 * 头像
	 */
	private String qqAvatar;

	/**
	 * 
	 */
	private String password;

	/**
	 * 注册时间
	 */
	@JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss",timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss")
	private Date joinTime;

	/**
	 * 上次登录时间
	 */
	@JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss",timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss")
	private Date lastLoginTime;

	/**
	 * 状态 0为禁用
	 */
	private Integer status;

	/**
	 * 使用空间
	 */
	private Long useSpace;

	/**
	 * 总空间
	 */
	@JsonIgnore
	private Long totalSpace;

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserId() {
		return this.userId;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getNickname() {
		return this.nickname;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmail() {
		return this.email;
	}

	public void setQqOpenId(String qqOpenId) {
		this.qqOpenId = qqOpenId;
	}

	public String getQqOpenId() {
		return this.qqOpenId;
	}

	public void setQqAvatar(String qqAvatar) {
		this.qqAvatar = qqAvatar;
	}

	public String getQqAvatar() {
		return this.qqAvatar;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return this.password;
	}

	public void setJoinTime(Date joinTime) {
		this.joinTime = joinTime;
	}

	public Date getJoinTime() {
		return this.joinTime;
	}

	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public Date getLastLoginTime() {
		return this.lastLoginTime;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getStatus() {
		return this.status;
	}

	public void setUseSpace(Long useSpace) {
		this.useSpace = useSpace;
	}

	public Long getUseSpace() {
		return this.useSpace;
	}

	public void setTotalSpace(Long totalSpace) {
		this.totalSpace = totalSpace;
	}

	public Long getTotalSpace() {
		return this.totalSpace;
	}

	@Override
	public String toString() {
		return "用户id:" + (userId == null ? "空" : userId) + ",昵称:" + (nickname == null ? "空" : nickname) + ",邮箱:" + (email == null ? "空" : email) + ",qqOpenId:" + (qqOpenId == null ? "空" : qqOpenId) + ",头像:" + (qqAvatar == null ? "空" : qqAvatar) + ",:" + (password == null ? "空" : password) + ",注册时间:" + (joinTime == null ? "空" : DateUtils.format(joinTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern())) + ",上次登录时间:" + (lastLoginTime == null ? "空" : DateUtils.format(lastLoginTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern())) + ",状态 0为禁用:" + (status == null ? "空" : status) + ",使用空间:" + (useSpace == null ? "空" : useSpace) + ",总空间:" + (totalSpace == null ? "空" : totalSpace);
	}
}