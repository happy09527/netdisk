package com.easypan.service.impl;

import com.easypan.entity.query.SimplePage;
import com.easypan.enums.PageSize;
import com.easypan.entity.vo.PaginationResultVo;
import com.easypan.entity.pojo.EmailCode;
import com.easypan.entity.query.EmailCodeQuery;
import com.easypan.mappers.EmailCodeMapper;
import com.easypan.service.EmailCodeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author hapZhang
 * @Description 邮箱验证码Service
 * @Date 2023/07/21 21:37:32
 */
@Service("emailCodeService")
public class EmailCodeServiceImpl implements EmailCodeService {

    @Resource
    private EmailCodeMapper<EmailCode, EmailCodeQuery> emailCodeMapper;

    /**
     * 根据条件查询列表
     */
    public List<EmailCode> findListByParam(EmailCodeQuery query) {
        return this.emailCodeMapper.selectList(query);
    }

    /**
     * 根据条件查询数量
     */
    public Integer findCountByParam(EmailCodeQuery query) {
        return this.emailCodeMapper.selectCount(query);
    }

    /**
     * 分页查询
     */
    public PaginationResultVo<EmailCode> findListByPage(EmailCodeQuery query) {
        Integer count = this.findCountByParam(query);
        Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
        SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
        query.setSimplePage(page);
        List<EmailCode> list = this.findListByParam(query);
        PaginationResultVo<EmailCode> result = new PaginationResultVo(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
        return result;
    }

    /**
     * 新增
     */
    public Integer add(EmailCode bean) {
        return this.emailCodeMapper.insert(bean);
    }

    /**
     * 批量新增
     */
    public Integer addBatch(List<EmailCode> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.emailCodeMapper.insertBatch(listBean);
    }

    /**
     * 批量新增或修改
     */
    public Integer addOrUpdateBatch(List<EmailCode> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.emailCodeMapper.insertOrUpdateBatch(listBean);
    }

    /**
     * 根据EmailAndCode查询
     */
    public EmailCode getEmailCodeByEmailAndCode(String email, String code) {
        return this.emailCodeMapper.selectByEmailAndCode(email, code);
    }

    /**
     * 根据EmailAndCode更新
     */
    public Integer updateEmailCodeByEmailAndCode(EmailCode bean, String email, String code) {
        return this.emailCodeMapper.updateByEmailAndCode(bean, email, code);
    }

    /**
     * 根据EmailAndCode删除
     */
    public Integer deleteEmailCodeByEmailAndCode(String email, String code) {
        return this.emailCodeMapper.deleteByEmailAndCode(email, code);
    }
}