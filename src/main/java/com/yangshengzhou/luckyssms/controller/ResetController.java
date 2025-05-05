package com.yangshengzhou.luckyssms.controller;

import com.yangshengzhou.luckyssms.dto.ResetRequest;
import com.yangshengzhou.luckyssms.dto.ResetResponse;
import com.yangshengzhou.luckyssms.mapper.ResetMapper;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ResetController {
    private final ResetMapper resetMapper;

    public ResetController(ResetMapper resetMapper) {
        this.resetMapper = resetMapper;
    }

    @Operation(summary = "重设密码", description = "处理用户的重置密码请求并返回是否成功")
    @PostMapping("api/reset")
    public ResponseEntity<ResetResponse> reset(@RequestBody ResetRequest resetRequest) {

        // 首先检查手机号是否存在
        if (!resetMapper.existsByPhone(resetRequest.getPhone())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResetResponse(false, "用户不存在"));
        }

        System.out.println(resetRequest.getPhone() + " " + resetRequest.getPassword());

        int affectedRows = resetMapper.resetPassword(resetRequest.getPhone(), resetRequest.getPassword());

        if (affectedRows > 0) {
            // 如果密码重置成功
            ResetResponse response = new ResetResponse(true, "重置成功");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            // 如果密码重置失败
            ResetResponse response = new ResetResponse(false, "重置失败");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
}