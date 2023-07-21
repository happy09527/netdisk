package com.easypan.mappers;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Author hapZhang
 * @Description 用户信息表
 * 存储用户登录信息、云盘空间大小Mapper
 * @Date 2023/07/21 14:42:24
 */
@Mapper
public interface UserInfoMapper<T, P> extends BaseMapper {
    /**
     * 根据UserId查询
     */
    T selectByUserId(@Param("userId") Integer userId);

    /**
     * 根据UserId更新
     */
    Integer updateByUserId(@Param("bean") T t, @Param("userId") Integer userId);

    /**
     * 根据UserId删除
     */
    Integer deleteByUserId(@Param("userId") Integer userId);

    /**
     * 根据Email查询
     */
    T selectByEmail(@Param("email") String email);

    /**
     * 根据Email更新
     */
    Integer updateByEmail(@Param("bean") T t, @Param("email") String email);

    /**
     * 根据Email删除
     */
    Integer deleteByEmail(@Param("email") String email);

    /**
     * 根据QqOpenId查询
     */
    T selectByQqOpenId(@Param("qqOpenId") String qqOpenId);

    /**
     * 根据QqOpenId更新
     */
    Integer updateByQqOpenId(@Param("bean") T t, @Param("qqOpenId") String qqOpenId);

    /**
     * 根据QqOpenId删除
     */
    Integer deleteByQqOpenId(@Param("qqOpenId") String qqOpenId);

    /**
     * 根据Nickname查询
     */
    T selectByNickname(@Param("nickname") String nickname);

    /**
     * 根据Nickname更新
     */
    Integer updateByNickname(@Param("bean") T t, @Param("nickname") String nickname);

    /**
     * 根据Nickname删除
     */
    Integer deleteByNickname(@Param("nickname") String nickname);


}