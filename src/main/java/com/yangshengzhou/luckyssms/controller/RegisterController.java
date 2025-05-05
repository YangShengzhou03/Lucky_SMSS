package com.yangshengzhou.luckyssms.controller;

import com.yangshengzhou.luckyssms.dto.RegisterRequest;
import com.yangshengzhou.luckyssms.dto.RegisterResponse;
import com.yangshengzhou.luckyssms.mapper.RegisterMapper;
import com.yangshengzhou.luckyssms.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegisterController {

    private final RegisterMapper registerMapper;
    private final JwtUtil jwtUtil;

    public RegisterController(RegisterMapper registerMapper, JwtUtil jwtUtil) {
        this.registerMapper = registerMapper;
        this.jwtUtil = jwtUtil;
    }

    @Operation(summary = "注册", description = "处理用户的注册请求并返回是否成功")
    @PostMapping("api/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest registerRequest) {
        System.out.println(registerRequest.getUid());
        System.out.println(registerRequest.getPhone());
        System.out.println(registerRequest.getVerificationCode());

        // 先检查用户是否已经存在
        if (registerMapper.userExists(registerRequest.getUid(), registerRequest.getPhone())) {
            RegisterResponse response = new RegisterResponse(false, "用户已存在，请登录", null);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response); // HTTP 409 Conflict
        }

        // 执行插入操作
        boolean result = registerMapper.insertUser(registerRequest.getUid(), registerRequest.getPhone());
        registerMapper.insertStudent(registerRequest.getUid());

        if (!result) {
            RegisterResponse response = new RegisterResponse(false, "注册失败", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        // 登录成功，生成Token
        registerMapper.updateLastLoginTime(registerRequest.getPhone());
        String token = jwtUtil.generateToken(Integer.toString(registerRequest.getUid()));

        RegisterResponse response = new RegisterResponse(true, "注册成功", token);
        return ResponseEntity.ok(response);
    }
}
