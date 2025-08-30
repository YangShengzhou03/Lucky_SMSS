package com.yangshengzhou.lucky_sms.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TeachingAssignment {
    private Integer assignmentId;        // 授课记录ID
    private Integer teacherId;          // 教师ID
    private Integer courseId;           // 课程ID
    private Integer semesterId;         // 学期ID
    private String classroom;          // 教室
    private String schedule;            // 上课时间安排
    private Integer maxStudents;        // 最大选课人数
    private Integer currentStudents;   // 当前选课人数
    private LocalDateTime createdAt;   // 创建时间
    private LocalDateTime updatedAt;    // 更新时间
}