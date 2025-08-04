package com.yangshengzhou.lucky_sms.mapper;

import com.yangshengzhou.lucky_sms.entity.User;
import org.apache.ibatis.annotations.Mapper;
import java.util.Map;

@Mapper
public interface UserMapper {
    // 登录查询，返回用户实体（查不到则返回null）
    User login(Map<String, Object> params);
}