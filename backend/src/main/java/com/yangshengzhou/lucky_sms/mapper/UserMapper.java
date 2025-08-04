package com.yangshengzhou.lucky_sms.mapper;

import com.yangshengzhou.lucky_sms.vo.LoginVO;
import org.apache.ibatis.annotations.Mapper;
import java.util.Map;

@Mapper
public interface UserMapper {
    // 直接返回LoginVO（包含角色信息），不再返回User实体
    LoginVO login(Map<String, Object> params);
}