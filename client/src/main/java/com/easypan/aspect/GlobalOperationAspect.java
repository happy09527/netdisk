package com.easypan.aspect;

import com.easypan.annotation.GlobalInterceptor;
import com.easypan.annotation.VerifyParam;
import com.easypan.entity.constants.Constants;
import com.easypan.entity.dto.SessionWebUserDto;
import com.easypan.entity.enums.ResponseCodeEnum;
import com.easypan.exception.BusinessException;
import com.easypan.utils.StringUtils;
import com.easypan.utils.VerifyUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @author: ZhangX
 * @createDate: 2023/7/22
 * @description:
 */
@Aspect
@Component("globalOperationAspect")
public class GlobalOperationAspect {
    private static final Logger logger = LoggerFactory.getLogger(GlobalOperationAspect.class);
    private static final String TYPE_STRING = "java.lang.String";
    private static final String TYPE_INTEGER = "java.lang.Integer";
    private static final String TYPE_LONG = "java.lang.Long";

    @Pointcut("@annotation(com.easypan.annotation.GlobalInterceptor)")
    private void requestInterceptor() {

    }

    @Before("requestInterceptor()")
    public void interceptorDo(JoinPoint joinPoint) throws BusinessException {
        try {
            // 目标类
            Object target = joinPoint.getTarget();
            // 目标参数
            Object[] arguments = joinPoint.getArgs();
            // 目标方法名称
            String methodName = joinPoint.getSignature().getName();
            // 参数类型
            Class<?>[] parameterTypes = ((MethodSignature) joinPoint.getSignature())
                    .getMethod().getParameterTypes();
            // 方法
            Method method = target.getClass().getMethod(methodName, parameterTypes);
            GlobalInterceptor interceptor = method.getAnnotation(GlobalInterceptor.class);
            if (interceptor == null) {
                return;
            }
            /**
             * 校验登陆
             */
            if (interceptor.checkLogin() || interceptor.checkAdmin()) {
                checkLogin(interceptor.checkAdmin());
            }
            /**
             * 校验参数
             */
            if (interceptor.checkParams()) {
                validateParams(method, arguments);
            }
        } catch (BusinessException e) {
            logger.error("全局拦截器异常", e);
            throw e;
        } catch (Exception e) {
            logger.error("全局拦截器异常", e);
            throw new BusinessException(ResponseCodeEnum.CODE_500);
        } catch (Throwable e) {
            logger.error("全局拦截器异常", e);
            throw new BusinessException(ResponseCodeEnum.CODE_500);
        }
    }

    /**
     * @date: 2023/7/24 16:06
     * 检验登录与管理员权限
     **/
    private void checkLogin(Boolean checkAdmin) {
        // springboot获取session信息
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpSession session = request.getSession();
        SessionWebUserDto webUserDto = (SessionWebUserDto) session.getAttribute(Constants.SESSION_KEY);
        if (webUserDto == null) {
            throw new BusinessException(ResponseCodeEnum.CODE_901);
        }
        if (checkAdmin && !webUserDto.getIsAdmin()) {
            throw new BusinessException(ResponseCodeEnum.CODE_404);
        }
    }

    /**
     * @date: 2023/7/22 16:17
     * 参数检验
     **/
    private void validateParams(Method method, Object[] arguments) {
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Object value = arguments[i];
            VerifyParam verifyParam = parameter.getAnnotation(VerifyParam.class);
            if (verifyParam == null) {
                continue;
            }
            if (TYPE_STRING.equals(parameter.getParameterizedType().getTypeName())
                    || TYPE_INTEGER.equals(parameter.getParameterizedType().getTypeName())
                    || TYPE_LONG.equals(parameter.getParameterizedType().getTypeName())) {
                checkValue(value, verifyParam);
            } else {
                checkObjValue(parameter, value);
            }
        }
    }

    /**
     * @date: 2023/7/22 16:17
     * 检验对象值
     **/
    private void checkObjValue(Parameter parameter, Object value) {
        try {
            String typeName = parameter.getParameterizedType().getTypeName();
            Class clazz = Class.forName(typeName);
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                VerifyParam fieldVerifyParam = field.getAnnotation(VerifyParam.class);
                if (fieldVerifyParam == null) {
                    continue;
                }
                field.setAccessible(true);
                Object resultValue = field.get(value);
                checkValue(resultValue, fieldVerifyParam);
            }
        } catch (BusinessException e) {
            logger.error("校验参数失败", e);
            throw e;
        } catch (Exception e) {
            logger.error("校验参数失败", e);
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
    }

    /**
     * 校验参数
     */
    private void checkValue(Object value, VerifyParam verifyParam) throws BusinessException {
        Boolean isEmpty = value == null || StringUtils.isEmpty(value.toString());
        Integer length = value == null ? 0 : value.toString().length();

        /**
         * 校验空
         */
        if (isEmpty && verifyParam.required()) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }

        /**
         * 校验长度
         */
        if (!isEmpty && (verifyParam.max() != -1 && verifyParam.max() < length
                || verifyParam.min() != -1 && verifyParam.min() > length)) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        /**
         * 校验正则
         */
        if (!isEmpty && !StringUtils.isEmpty(verifyParam.regex().getRegex())
                && !VerifyUtils.verify(verifyParam.regex(), String.valueOf(value))) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
    }
}