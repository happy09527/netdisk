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

	/**
	 * 根据条件查询列表
	 */
	List<EmailCode> findListByParam(EmailCodeQuery query);

	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(EmailCodeQuery query);

	/**
	 * 分页查询
	 */
	PaginationResultVo<EmailCode> findListByPage(EmailCodeQuery query);

	/**
	 * 新增
	 */
	Integer add(EmailCode bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<EmailCode> listBean);

	/**
	 * 批量新增或修改
	 */
	Integer addOrUpdateBatch(List<EmailCode> listBean);

	/**
	 * 根据EmailAndCode查询
	 */
	EmailCode getEmailCodeByEmailAndCode(String email, String code);

	/**
	 * 根据EmailAndCode更新
	 */
	Integer updateEmailCodeByEmailAndCode(EmailCode bean,String email, String code);

	/**
	 * 根据EmailAndCode删除
	 */
	Integer deleteEmailCodeByEmailAndCode(String email, String code);

}