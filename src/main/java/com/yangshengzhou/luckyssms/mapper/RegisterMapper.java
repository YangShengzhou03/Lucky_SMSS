package com.yangshengzhou.luckyssms.mapper;

import org.apache.ibatis.annotations.*;

@Mapper
public interface RegisterMapper {

    @Insert("INSERT INTO users (uid, phone, avatarUrl) VALUES (#{uid}, #{phone}, " +
            "'http://mms2.baidu.com/it/u=3908971299,3813608292&fm=253')")
    boolean insertUser(@Param("uid") int uid, @Param("phone") String phone);

    @Insert("INSERT INTO students (uid, enrollmentDate) VALUES (#{uid}, '2025-05-29')")
    void insertStudent(@Param("uid") int uid);

    @Select("SELECT EXISTS(SELECT 1 FROM users WHERE uid = #{uid} OR phone = #{phone})")
    boolean userExists(@Param("uid") int uid, @Param("phone") String phone);

    @Update("UPDATE users SET lastLogin = NOW() WHERE phone = #{phone}")
    void updateLastLoginTime(@Param("phone") String phone);
}