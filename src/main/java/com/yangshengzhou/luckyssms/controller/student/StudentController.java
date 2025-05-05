package com.yangshengzhou.luckyssms.controller.student;

import com.yangshengzhou.luckyssms.dto.student.InitResponse;
import com.yangshengzhou.luckyssms.dto.student.UpdateAvatarResponse;
import com.yangshengzhou.luckyssms.dto.student.UpdateRequest;
import com.yangshengzhou.luckyssms.dto.student.UpdateResponse;
import com.yangshengzhou.luckyssms.mapper.student.StudentMapper;
import com.yangshengzhou.luckyssms.model.Student;
import com.yangshengzhou.luckyssms.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/student")
public class StudentController {

    private final JwtUtil jwtUtil;
    private final StudentMapper studentMapper;

    @Autowired
    public StudentController(JwtUtil jwtUtil, StudentMapper studentMapper) {
        this.jwtUtil = jwtUtil;
        this.studentMapper = studentMapper;
    }

    /**
     * 初始化学生数据接口。
     *
     * @param authorizationHeader 授权头，包含JWT令牌
     * @return 学生初始化数据响应实体
     */
    @Operation(summary = "初始化数据", description = "返回学生基本数据")
    @GetMapping("/init")
    public ResponseEntity<InitResponse> init(@RequestHeader("Authorization") String authorizationHeader) {
        int uid = parseUidFromToken(authorizationHeader);

        // 查询用户信息
        Student student = studentMapper.findByUid(uid);
        if (student == null) {
            throw new RuntimeException("未找到该用户");
        }

        // 构建并返回初始化响应
        InitResponse response = new InitResponse.Builder()
                .avatarUrl(student.getAvatarUrl())
                .name(student.getName() != null ? student.getName() : "YourName")
                .uid(student.getUid())
                .phone(student.getPhone() != null ? student.getPhone() : "")
                .email(student.getEmail() != null ? student.getEmail() : "")
                .address(student.getAddress() != null ? student.getAddress() : "")
                .college(student.getCollege() != null ? student.getCollege() : "")
                .major(student.getMajor() != null ? student.getMajor() : "")
                .enrollmentDate(student.getEnrollmentDate())
                .creditsCompleted(student.getCreditsCompleted())
                .creditsRequired(student.getCreditsRequired())
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * 更新学生头像接口。
     *
     * @param authorizationHeader 授权头，包含JWT令牌
     * @return 头像更新响应实体
     */
    @Operation(summary = "更新头像", description = "返回成功")
    @PostMapping("/updateAvatar")
    public ResponseEntity<UpdateAvatarResponse> updateAvatar(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam("avatarUrl") String avatarUrl) {  // 修改为接收字符串类型的 avatarUrl

        int uid = parseUidFromToken(authorizationHeader);

        try {
            studentMapper.updateAvatar(uid, avatarUrl);  // 假设你的 Mapper 支持直接更新 URL 字符串
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new UpdateAvatarResponse(false, "更新失败：" + e.getMessage()));
        }

        return ResponseEntity.ok(new UpdateAvatarResponse(true, "头像更新成功"));
    }

    @Operation(summary = "更新用户信息", description = "返回成功与否")
    @PostMapping("/update")
    public ResponseEntity<UpdateResponse> updateInfo(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody UpdateRequest updateRequest) {

        try {
            int uid = parseUidFromToken(authorizationHeader);

            // 调用 Mapper 更新信息
            studentMapper.updateStudentInfo(uid,
                    updateRequest.getName(),
                    updateRequest.getGender(),
                    updateRequest.getBirthDate(),
                    updateRequest.getAddress());

            studentMapper.updateUserContact(uid,
                    updateRequest.getPhone(),
                    updateRequest.getEmail()
            );

            return ResponseEntity.ok(new UpdateResponse(true, "用户信息更新成功"));
        } catch (Exception e) {
            return ResponseEntity.ok(new UpdateResponse(false, "更新失败：" + e.getMessage()));
        }
    }

    /**
     * 解析授权头中的JWT令牌，并验证其有效性，提取UID。
     *
     * @param authorizationHeader 授权头，包含JWT令牌
     * @return 用户ID
     */
    private int parseUidFromToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token 不能为空");
        }

        String token = authorizationHeader.substring(7);
        if (!jwtUtil.validateToken(token, jwtUtil.extractUid(token))) {
            throw new RuntimeException("无效 Token");
        }

        return Integer.parseInt(jwtUtil.extractUid(token));
    }
}