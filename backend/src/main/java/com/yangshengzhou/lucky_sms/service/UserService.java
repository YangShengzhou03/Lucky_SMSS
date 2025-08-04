package com.yangshengzhou.lucky_sms.service;

import com.yangshengzhou.lucky_sms.entity.User;
import com.yangshengzhou.lucky_sms.mapper.UserMapper;
import com.yangshengzhou.lucky_sms.vo.LoginVO;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class UserService {

    @Resource
    private UserMapper userMapper;

    public LoginVO login(String phone, String keyhash) {
        // 1. 参数校验（企业项目必做）
        if (phone == null || keyhash == null) {
            throw new IllegalArgumentException("手机号或密码不能为空");
        }

        // 2. 调用Mapper查询用户
        Map<String, Object> params = new HashMap<>();
        params.put("phone", phone);
        params.put("keyhash", keyhash);
        User user = userMapper.login(params);

        // 3. 处理登录结果
        if (user == null) {
            throw new RuntimeException("手机号或密码错误");
        }

        // 4. 生成令牌（用JWT）
        String token = UUID.randomUUID().toString();

        // 5. 构建返回VO（脱敏处理）
        LoginVO loginVO = new LoginVO();
        loginVO.setUid(user.getUserId());
        loginVO.setUsername(user.getUsername());
        loginVO.setPhone(desensitizePhone(user.getPhone())); // 手机号脱敏
        loginVO.setRole(user.getRole());
        loginVO.setToken(token);

        return loginVO;
    }

    // 手机号脱敏（如13812345678 → 138****5678）
    private String desensitizePhone(String phone) {
        if (phone == null || phone.length() != 11) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }
}