package com.easypan.service;

import com.easypan.entity.dto.SessionWebUserDto;
import com.easypan.entity.vo.PaginationResultVo;
import com.easypan.entity.pojo.UserInfo;
import com.easypan.entity.query.UserInfoQuery;

import java.util.List;
/**
 * @Author hapZhang
 * @Description 用户信息表
存储用户登录信息、云盘空间大小Service
 * @Date 2023/07/21 14:45:18
 */
public interface UserInfoService{

	/**
	 * 根据条件查询列表
	 */
	List<UserInfo> findListByParam(UserInfoQuery query);

	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(UserInfoQuery query);

	/**
	 * 分页查询
	 */
	PaginationResultVo<UserInfo> findListByPage(UserInfoQuery query);

	/**
	 * 新增
	 */
	Integer add(UserInfo bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<UserInfo> listBean);

	/**
	 * 批量新增或修改
	 */
	Integer addOrUpdateBatch(List<UserInfo> listBean);

	/**
	 * 根据UserId查询
	 */
	UserInfo getUserInfoByUserId(Integer userId);

	/**
	 * 根据UserId更新
	 */
	Integer updateUserInfoByUserId(UserInfo bean,Integer userId);

	/**
	 * 根据UserId删除
	 */
	Integer deleteUserInfoByUserId(Integer userId);

	/**
	 * 根据Email查询
	 */
	UserInfo getUserInfoByEmail(String email);

	/**
	 * 根据Email更新
	 */
	Integer updateUserInfoByEmail(UserInfo bean,String email);

	/**
	 * 根据Email删除
	 */
	Integer deleteUserInfoByEmail(String email);

	/**
	 * 根据QqOpenId查询
	 */
	UserInfo getUserInfoByQqOpenId(String qqOpenId);

	/**
	 * 根据QqOpenId更新
	 */
	Integer updateUserInfoByQqOpenId(UserInfo bean,String qqOpenId);

	/**
	 * 根据QqOpenId删除
	 */
	Integer deleteUserInfoByQqOpenId(String qqOpenId);

	/**
	 * 根据Nickname查询
	 */
	UserInfo getUserInfoByNickname(String nickname);

	/**
	 * 根据Nickname更新
	 */
	Integer updateUserInfoByNickname(UserInfo bean,String nickname);

	/**
	 * 根据Nickname删除
	 */
	Integer deleteUserInfoByNickname(String nickname);

    SessionWebUserDto login(String email, String password);

	void register(String email, String nickName, String password, String emailCode);
}