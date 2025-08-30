package com.yangshengzhou.lucky_sms.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CourseSelection {
    private Integer selectionId;       // 选课记录ID
    private Integer studentId;         // 学生ID
    private Integer assignmentId;      // 授课记录ID
    private LocalDateTime selectionDate; // 选课日期
    private String status;             // 选课状态：SELECTED/DROPPED/COMPLETED
    private LocalDateTime createdAt;   // 创建时间
    private LocalDateTime updatedAt;    // 更新时间
}