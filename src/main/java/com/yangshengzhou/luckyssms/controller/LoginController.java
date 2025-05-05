package com.yangshengzhou.luckyssms.controller;

import com.yangshengzhou.luckyssms.dto.LoginRequest;
import com.yangshengzhou.luckyssms.dto.LoginResponse;
import com.yangshengzhou.luckyssms.mapper.LoginMapper;
import com.yangshengzhou.luckyssms.model.User;
import com.yangshengzhou.luckyssms.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class LoginController {

    private final LoginMapper loginMapper;
    private final JwtUtil jwtUtil;

    public LoginController(LoginMapper loginMapper, JwtUtil jwtUtil) {
        this.loginMapper = loginMapper;
        this.jwtUtil = jwtUtil;
    }

    @Operation(summary = "登录", description = "处理用户的登录请求并返回用户角色")
    @PostMapping("/api/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {

        List<User> users = loginMapper.findUserByPhone(loginRequest.getPhone());

        if (users.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new LoginResponse(false, null, "账号不存在", null));
        }

        User user = users.get(0);
        if (!user.getPassword().equals(loginRequest.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponse(false, null, "密码错误", null));
        }

        if (!loginMapper.checkStatus(user.getPhone())){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new LoginResponse(false, null, "账号被禁用", null));
        }

        // 登录成功，生成Token
        loginMapper.updateLastLoginTime(user.getPhone());
        String token = jwtUtil.generateToken(Integer.toString(user.getUid()));

        // 创建包含Token的响应对象
        LoginResponse response = new LoginResponse(true, user.getRole(), "登录成功", token);

        return ResponseEntity.ok(response);
    }
}