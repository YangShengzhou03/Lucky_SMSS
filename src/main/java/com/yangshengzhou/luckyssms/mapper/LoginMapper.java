package com.yangshengzhou.luckyssms.mapper;

import com.yangshengzhou.luckyssms.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface LoginMapper {

    @Select("SELECT * FROM users WHERE phone = #{phone}")
    List<User> findUserByPhone(@Param("phone") String phone);

    @Update("UPDATE users SET lastLogin = NOW() WHERE phone = #{phone}")
    void updateLastLoginTime(@Param("phone") String phone);

    @Select("SELECT status FROM users WHERE phone = #{phone}")
    boolean checkStatus(@Param("phone") String phone);
}