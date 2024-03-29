package com.easypan.mappers;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Author hapZhang
 * @Description 邮箱验证码Mapper
 * @Date 2023/07/21 21:37:32
 */
@Mapper
public interface EmailCodeMapper<T, P> extends BaseMapper {
    /**
     * 根据EmailAndCode查询
     */
    T selectByEmailAndCode(@Param("email") String email, @Param("code") String code);

    /**
     * 根据EmailAndCode更新
     */
    Integer updateByEmailAndCode(@Param("bean") T t, @Param("email") String email, @Param("code") String code);

    /**
     * 根据EmailAndCode删除
     */
    Integer deleteByEmailAndCode(@Param("email") String email, @Param("code") String code);


    void disableEmailCode(@Param("email") String email);
}