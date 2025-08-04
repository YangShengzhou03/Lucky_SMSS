package com.yangshengzhou.lucky_sms.controller;

import com.yangshengzhou.lucky_sms.service.UserService;
import com.yangshengzhou.lucky_sms.vo.LoginVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RestController
public class LoginController {

    @Resource
    private UserService userService;

    @PostMapping("/login")
    public Map<String, Object> login(
            @RequestParam String phone,
            @RequestParam String keyhash
    ) {
        Map<String, Object> result = new HashMap<>();
        try {
            LoginVO loginVO = userService.login(phone, keyhash);
            result.put("code", 200);       // 状态码：200成功
            result.put("message", "登录成功");
            result.put("data", loginVO);   // 响应数据
        } catch (Exception e) {
            result.put("code", 400);       // 状态码：400失败
            result.put("message", e.getMessage());
            result.put("data", null);
        }
        return result;
    }
}