package com.yangshengzhou.lucky_sms.entity;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data  // Lombok注解，自动生成getter、setter、toString等方法
public class User {
    private Integer userId;              // 用户ID，自增
    private String username;             // 用户名
    private String passwordHash;         // 密码哈希
    private String email;                // 邮箱
    private String phone;                // 手机号
    private String gender;               // 性别，取值为'M'、'F'、'O'
    private LocalDate birthDate;         // 出生日期
    private String avatarUrl;            // 头像URL
    private String externalId;           // 外部ID
    private String status;               // 状态，默认为'ACTIVE'，可选'INACTIVE'
    private LocalDateTime lastLoginTime; // 最后登录时间
    private String lastLoginIp;          // 最后登录IP
    private LocalDateTime lastPasswordChangeTime; // 最后密码修改时间
    private LocalDateTime createdAt;     // 创建时间，默认当前时间
    private LocalDateTime updatedAt;     // 更新时间，默认当前时间
    private String role;                 // 登录用户的角色
}
