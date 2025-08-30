package com.yangshengzhou.lucky_sms.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Course {
    private Integer courseId;              // 课程ID
    private String courseCode;            // 课程代码
    private String courseName;            // 课程名称
    private String courseDescription;     // 课程描述
    private Integer departmentId;         // 所属学院ID
    private Double credit;                // 学分
    private Integer courseHours;          // 课时
    private String courseType;            // 课程类型：COMPULSORY/ELECTIVE
    private String examType;             // 考试类型
    private Integer createdBy;            // 创建人ID
    private LocalDateTime createdAt;     // 创建时间
    private LocalDateTime updatedAt;     // 更新时间
}