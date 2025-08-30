package com.yangshengzhou.lucky_sms.controller;

import com.yangshengzhou.lucky_sms.service.CourseSelectionService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/course")
public class CourseController {

    @Resource
    private CourseSelectionService courseSelectionService;

    /**
     * 抢课接口
     */
    @PostMapping("/select")
    public Map<String, Object> selectCourse(
            @RequestParam Integer studentId,
            @RequestParam Integer assignmentId) {
        
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = courseSelectionService.selectCourse(studentId, assignmentId);
            if (success) {
                result.put("code", 200);
                result.put("message", "选课成功");
                result.put("data", null);
            } else {
                result.put("code", 400);
                result.put("message", "选课失败");
                result.put("data", null);
            }
        } catch (Exception e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
            result.put("data", null);
        }
        return result;
    }

    /**
     * 退课接口
     */
    @PostMapping("/drop")
    public Map<String, Object> dropCourse(
            @RequestParam Integer studentId,
            @RequestParam Integer assignmentId) {
        
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = courseSelectionService.dropCourse(studentId, assignmentId);
            if (success) {
                result.put("code", 200);
                result.put("message", "退课成功");
                result.put("data", null);
            } else {
                result.put("code", 400);
                result.put("message", "退课失败");
                result.put("data", null);
            }
        } catch (Exception e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
            result.put("data", null);
        }
        return result;
    }

    /**
     * 查询课程剩余容量
     */
    @GetMapping("/capacity/{assignmentId}")
    public Map<String, Object> getCourseCapacity(@PathVariable Integer assignmentId) {
        Map<String, Object> result = new HashMap<>();
        try {
            Integer remainingCapacity = courseSelectionService.getCourseRemainingCapacity(assignmentId);
            result.put("code", 200);
            result.put("message", "查询成功");
            result.put("data", remainingCapacity);
        } catch (Exception e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
            result.put("data", null);
        }
        return result;
    }

    /**
     * 查询可选课程列表
     */
    @GetMapping("/available")
    public Map<String, Object> getAvailableCourses() {
        Map<String, Object> result = new HashMap<>();
        try {
            // TODO: 实现查询可选课程列表的逻辑
            result.put("code", 200);
            result.put("message", "查询成功");
            result.put("data", null);
        } catch (Exception e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
            result.put("data", null);
        }
        return result;
    }
}