package com.easypan.service.impl;

import com.easypan.entity.query.SimplePage;
import com.easypan.enums.PageSize;
import com.easypan.entity.vo.PaginationResultVo;
import com.easypan.entity.pojo.UserInfo;
import com.easypan.entity.query.UserInfoQuery;
import com.easypan.mappers.UserInfoMapper;
import com.easypan.service.UserInfoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
/**
 * @Author hapZhang
 * @Description 用户信息表
存储用户登录信息、云盘空间大小Service
 * @Date 2023/07/21 14:42:24
 */
@Service("userInfoService")
public class UserInfoServiceImpl implements UserInfoService{

	@Resource
	private UserInfoMapper<UserInfo,UserInfoQuery> userInfoMapper;

	/**
	 * 根据条件查询列表
	 */
	public List<UserInfo> findListByParam(UserInfoQuery query) {
		return this.userInfoMapper.selectList(query);
	}

	/**
	 * 根据条件查询数量
	 */
	public Integer findCountByParam(UserInfoQuery query) {
		return this.userInfoMapper.selectCount(query);
	}

	/**
	 * 分页查询
	 */
	public PaginationResultVo<UserInfo> findListByPage(UserInfoQuery query) {
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
		query.setSimplePage(page);
		List<UserInfo> list = this.findListByParam(query);
		PaginationResultVo<UserInfo> result = new PaginationResultVo(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	/**
	 * 新增
	 */
	public Integer add(UserInfo bean) {
		return this.userInfoMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	public Integer addBatch(List<UserInfo> listBean) {
		if(listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.userInfoMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或修改
	 */
	public Integer addOrUpdateBatch(List<UserInfo> listBean) {
		if(listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.userInfoMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 根据UserId查询
	 */
	public UserInfo getUserInfoByUserId(Integer userId) {
		return this.userInfoMapper.selectByUserId(userId);
	}

	/**
	 * 根据UserId更新
	 */
	public Integer updateUserInfoByUserId(UserInfo bean,Integer userId) {
		return this.userInfoMapper.updateByUserId(bean,userId);
	}

	/**
	 * 根据UserId删除
	 */
	public Integer deleteUserInfoByUserId(Integer userId) {
		return this.userInfoMapper.deleteByUserId(userId);
	}

	/**
	 * 根据Email查询
	 */
	public UserInfo getUserInfoByEmail(String email) {
		return this.userInfoMapper.selectByEmail(email);
	}

	/**
	 * 根据Email更新
	 */
	public Integer updateUserInfoByEmail(UserInfo bean,String email) {
		return this.userInfoMapper.updateByEmail(bean,email);
	}

	/**
	 * 根据Email删除
	 */
	public Integer deleteUserInfoByEmail(String email) {
		return this.userInfoMapper.deleteByEmail(email);
	}

	/**
	 * 根据QqOpenId查询
	 */
	public UserInfo getUserInfoByQqOpenId(String qqOpenId) {
		return this.userInfoMapper.selectByQqOpenId(qqOpenId);
	}

	/**
	 * 根据QqOpenId更新
	 */
	public Integer updateUserInfoByQqOpenId(UserInfo bean,String qqOpenId) {
		return this.userInfoMapper.updateByQqOpenId(bean,qqOpenId);
	}

	/**
	 * 根据QqOpenId删除
	 */
	public Integer deleteUserInfoByQqOpenId(String qqOpenId) {
		return this.userInfoMapper.deleteByQqOpenId(qqOpenId);
	}

	/**
	 * 根据Nickname查询
	 */
	public UserInfo getUserInfoByNickname(String nickname) {
		return this.userInfoMapper.selectByNickname(nickname);
	}

	/**
	 * 根据Nickname更新
	 */
	public Integer updateUserInfoByNickname(UserInfo bean,String nickname) {
		return this.userInfoMapper.updateByNickname(bean,nickname);
	}

	/**
	 * 根据Nickname删除
	 */
	public Integer deleteUserInfoByNickname(String nickname) {
		return this.userInfoMapper.deleteByNickname(nickname);
	}

}