package com.easypan.entity.query;

import java.util.Date;


/**
 * @Author hapZhang
 * @Description 用户信息表
存储用户登录信息、云盘空间大小查询对象
 * @Date 2023/07/21 14:42:24
 */
public class UserInfoQuery extends BaseQuery {
	/**
	 * 用户id
	 */
	private Integer userId;
	/**
	 * 昵称
	 */
	private String nickname;
	private String nicknameFuzzy;

	/**
	 * 邮箱
	 */
	private String email;
	private String emailFuzzy;

	/**
	 * qqOpenId
	 */
	private String qqOpenId;
	private String qqOpenIdFuzzy;

	/**
	 * 头像
	 */
	private String qqAvatar;
	private String qqAvatarFuzzy;

	/**
	 * 
	 */
	private String password;
	private String passwordFuzzy;

	/**
	 * 注册时间
	 */
	private Date joinTime;
	private String joinTimeStart;

	private String joinTimeEnd;

	/**
	 * 上次登录时间
	 */
	private Date lastLoginTime;
	private String lastLoginTimeStart;

	private String lastLoginTimeEnd;

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
	private Long totalSpace;
	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getUserId() {
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

	public void setNicknameFuzzy(String nicknameFuzzy) {
		this.nicknameFuzzy = nicknameFuzzy;
	}

	public String getNicknameFuzzy() {
		return this.nicknameFuzzy;
	}

	public void setEmailFuzzy(String emailFuzzy) {
		this.emailFuzzy = emailFuzzy;
	}

	public String getEmailFuzzy() {
		return this.emailFuzzy;
	}

	public void setQqOpenIdFuzzy(String qqOpenIdFuzzy) {
		this.qqOpenIdFuzzy = qqOpenIdFuzzy;
	}

	public String getQqOpenIdFuzzy() {
		return this.qqOpenIdFuzzy;
	}

	public void setQqAvatarFuzzy(String qqAvatarFuzzy) {
		this.qqAvatarFuzzy = qqAvatarFuzzy;
	}

	public String getQqAvatarFuzzy() {
		return this.qqAvatarFuzzy;
	}

	public void setPasswordFuzzy(String passwordFuzzy) {
		this.passwordFuzzy = passwordFuzzy;
	}

	public String getPasswordFuzzy() {
		return this.passwordFuzzy;
	}

	public void setJoinTimeStart(String joinTimeStart) {
		this.joinTimeStart = joinTimeStart;
	}

	public String getJoinTimeStart() {
		return this.joinTimeStart;
	}

	public void setJoinTimeEnd(String joinTimeEnd) {
		this.joinTimeEnd = joinTimeEnd;
	}

	public String getJoinTimeEnd() {
		return this.joinTimeEnd;
	}

	public void setLastLoginTimeStart(String lastLoginTimeStart) {
		this.lastLoginTimeStart = lastLoginTimeStart;
	}

	public String getLastLoginTimeStart() {
		return this.lastLoginTimeStart;
	}

	public void setLastLoginTimeEnd(String lastLoginTimeEnd) {
		this.lastLoginTimeEnd = lastLoginTimeEnd;
	}

	public String getLastLoginTimeEnd() {
		return this.lastLoginTimeEnd;
	}

}