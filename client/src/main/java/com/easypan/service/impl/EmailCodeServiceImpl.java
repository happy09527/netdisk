package com.easypan.service.impl;

import com.easypan.component.RedisComponent;
import com.easypan.config.APPConfig;
import com.easypan.entity.constants.Constants;
import com.easypan.entity.pojo.UserInfo;
import com.easypan.entity.query.SimplePage;
import com.easypan.entity.query.UserInfoQuery;
import com.easypan.enums.PageSize;
import com.easypan.entity.vo.PaginationResultVo;
import com.easypan.entity.pojo.EmailCode;
import com.easypan.entity.query.EmailCodeQuery;
import com.easypan.exception.BusinessException;
import com.easypan.mappers.EmailCodeMapper;
import com.easypan.mappers.UserInfoMapper;
import com.easypan.service.EmailCodeService;
import com.easypan.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.List;

/**
 * @Author hapZhang
 * @Description 邮箱验证码Service
 * @Date 2023/07/21 21:37:32
 */
@Service("emailCodeService")
public class EmailCodeServiceImpl implements EmailCodeService {
    private static final Logger logger = LoggerFactory.getLogger(EmailCodeServiceImpl.class);
    @Resource
    private EmailCodeMapper<EmailCode, EmailCodeQuery> emailCodeMapper;
    @Resource
    private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;
    @Resource
    private JavaMailSender javaMailSender;
    @Resource
    private APPConfig appConfig;
    @Resource
    private RedisComponent redisComponent;

    /**
     * @param type 0:注册  1:找回密码
     * @date: 2023/7/22 9:07
     * @description: 发送邮箱验证码的前置和后置工作
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendEmailCode(String email, Integer type) {
        if (type == 0) {
            UserInfo userInfo = userInfoMapper.selectByEmail(email);
            if (userInfo != null) {
                throw new BusinessException("邮箱已存在");
            }
        }
        // 数据库中的其他验证码置于不可用
        emailCodeMapper.disableEmailCode(email);

        String code = StringUtils.getRandomNumber(Constants.LENGTH_5);
        sendEmailCode(email, code);
        EmailCode emailCode = new EmailCode();
        emailCode.setCode(code);
        emailCode.setEmail(email);
        emailCode.setStatus(1);
        emailCode.setCreateTime(new Date());
        emailCodeMapper.insert(emailCode);
    }
    /**
     * @date: 2023/7/22 22:47
     * 验证邮箱验证码是否正确
     **/
    @Override
    public void checkCode(String email, String code) {
        EmailCode emailCode = this.emailCodeMapper.selectByEmailAndCode(email,code);
        if(emailCode == null){
            throw  new BusinessException("邮箱验证不正确");
        }
        if(emailCode.getStatus()==1 || System.currentTimeMillis()-emailCode.getCreateTime().getTime() > Constants.LENGTH_15 * 60 * 1000 ){
            throw new BusinessException("邮箱验证码失效");
        }
        emailCodeMapper.disableEmailCode(email);
    }

    /**
     * @date: 2023/7/22 10:30
     * @description: 发送邮箱验证码
     **/
    private void sendEmailCode(String toEmail, String code) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = null;
        try {
            mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            // 发件人
            mimeMessageHelper.setFrom(appConfig.getSendUserName());
            // 收件人
            mimeMessageHelper.setTo(toEmail);
            // 发送主题
            mimeMessageHelper.setSubject(redisComponent.getSysSettingDto().getRegisterEmailTitle());
            // 发送内容
            mimeMessageHelper.setText(String.format(redisComponent.getSysSettingDto().getRegisterEmailContent(), code));
            System.out.println(redisComponent.getSysSettingDto()+"aaaaaaaaaaaaaaaaaaaa");
            // 发送时间
            mimeMessageHelper.setSentDate(new Date());
            // 发送
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            logger.error("邮件发送失败", e);
            throw new RuntimeException("邮件发送异常");
        }

    }
}