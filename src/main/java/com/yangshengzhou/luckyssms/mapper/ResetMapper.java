package com.yangshengzhou.luckyssms.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ResetMapper {

    /**
     * 根据手机号码检查用户是否存在。
     *
     * @param phone 手机号码
     * @return 用户存在返回true，否则false
     */
    @Select("SELECT EXISTS(SELECT 1 FROM users WHERE phone = #{phone})")
    boolean existsByPhone(@Param("phone") String phone);

    /**
     * 根据手机号码重置用户密码。
     *
     * @param phone 手机号码
     * @param password 新密码
     * @return 影响的行数，通常为1表示成功
     */
    @Update("UPDATE users SET password = #{password} WHERE phone = #{phone}")
    int resetPassword(@Param("phone") String phone, @Param("password") String password);
}