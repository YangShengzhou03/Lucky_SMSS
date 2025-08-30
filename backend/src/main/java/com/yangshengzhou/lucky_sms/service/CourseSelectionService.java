package com.yangshengzhou.lucky_sms.service;

import com.yangshengzhou.lucky_sms.entity.CourseSelection;
import com.yangshengzhou.lucky_sms.mapper.CourseSelectionMapper;
import com.yangshengzhou.lucky_sms.utils.RedisUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class CourseSelectionService {

    @Resource
    private CourseSelectionMapper courseSelectionMapper;

    @Resource
    private RedisUtil redisUtil;

    // 课程容量前缀
    private static final String COURSE_CAPACITY_PREFIX = "course:capacity:";
    // 选课锁前缀
    private static final String SELECTION_LOCK_PREFIX = "selection:lock:";
    // 已选课程集合前缀
    private static final String SELECTED_COURSES_PREFIX = "selected:courses:";

    /**
     * 抢课核心方法
     */
    @Transactional
    public boolean selectCourse(Integer studentId, Integer assignmentId) {
        String lockKey = SELECTION_LOCK_PREFIX + assignmentId + ":" + studentId;
        String lockValue = UUID.randomUUID().toString();

        try {
            // 获取分布式锁，防止重复提交
            boolean locked = redisUtil.getLock(lockKey, lockValue, 10, TimeUnit.SECONDS);
            if (!locked) {
                throw new RuntimeException("操作过于频繁，请稍后再试");
            }

            // 检查课程容量
            String capacityKey = COURSE_CAPACITY_PREFIX + assignmentId;
            Integer currentCapacity = (Integer) redisUtil.get(capacityKey);
            
            if (currentCapacity == null) {
                // 从数据库初始化容量到Redis
                Integer maxStudents = courseSelectionMapper.getMaxStudentsByAssignmentId(assignmentId);
                Integer currentStudents = courseSelectionMapper.getCurrentStudentsByAssignmentId(assignmentId);
                currentCapacity = maxStudents - currentStudents;
                redisUtil.set(capacityKey, currentCapacity, 1, TimeUnit.HOURS);
            }

            if (currentCapacity <= 0) {
                throw new RuntimeException("课程已满，无法选课");
            }

            // 检查学生是否已经选过该课程
            String selectedKey = SELECTED_COURSES_PREFIX + studentId;
            if (Boolean.TRUE.equals(redisUtil.isMember(selectedKey, assignmentId))) {
                throw new RuntimeException("您已经选过该课程");
            }

            // 使用Redis原子操作减少容量
            Long newCapacity = redisUtil.decrement(capacityKey, 1);
            if (newCapacity != null && newCapacity >= 0) {
                // 记录学生已选课程
                redisUtil.addToSet(selectedKey, assignmentId);
                redisUtil.expire(selectedKey, 1, TimeUnit.HOURS);

                // 插入选课记录到数据库
                CourseSelection selection = new CourseSelection();
                selection.setStudentId(studentId);
                selection.setAssignmentId(assignmentId);
                selection.setStatus("SELECTED");
                
                int result = courseSelectionMapper.insert(selection);
                if (result > 0) {
                    // 更新数据库中的当前选课人数
                    courseSelectionMapper.incrementCurrentStudents(assignmentId);
                    return true;
                }
            }

            // 如果数据库操作失败，恢复Redis容量
            redisUtil.increment(capacityKey, 1);
            redisUtil.removeFromSet(selectedKey, assignmentId);
            return false;

        } finally {
            // 释放锁
            redisUtil.releaseLock(lockKey, lockValue);
        }
    }

    /**
     * 退课方法
     */
    @Transactional
    public boolean dropCourse(Integer studentId, Integer assignmentId) {
        String lockKey = SELECTION_LOCK_PREFIX + assignmentId + ":" + studentId;
        String lockValue = UUID.randomUUID().toString();

        try {
            // 获取分布式锁
            boolean locked = redisUtil.getLock(lockKey, lockValue, 10, TimeUnit.SECONDS);
            if (!locked) {
                throw new RuntimeException("操作过于频繁，请稍后再试");
            }

            // 检查学生是否选过该课程
            CourseSelection selection = courseSelectionMapper.findByStudentAndAssignment(studentId, assignmentId);
            if (selection == null || !"SELECTED".equals(selection.getStatus())) {
                throw new RuntimeException("未找到选课记录");
            }

            // 更新数据库选课状态
            int result = courseSelectionMapper.updateStatus(selection.getSelectionId(), "DROPPED");
            if (result > 0) {
                // 减少数据库当前选课人数
                courseSelectionMapper.decrementCurrentStudents(assignmentId);

                // 更新Redis容量
                String capacityKey = COURSE_CAPACITY_PREFIX + assignmentId;
                redisUtil.increment(capacityKey, 1);

                // 从已选课程集合中移除
                String selectedKey = SELECTED_COURSES_PREFIX + studentId;
                redisUtil.removeFromSet(selectedKey, assignmentId);

                return true;
            }

            return false;

        } finally {
            // 释放锁
            redisUtil.releaseLock(lockKey, lockValue);
        }
    }

    /**
     * 获取课程剩余容量
     */
    public Integer getCourseRemainingCapacity(Integer assignmentId) {
        String capacityKey = COURSE_CAPACITY_PREFIX + assignmentId;
        Integer capacity = (Integer) redisUtil.get(capacityKey);
        
        if (capacity == null) {
            // 从数据库初始化
            Integer maxStudents = courseSelectionMapper.getMaxStudentsByAssignmentId(assignmentId);
            Integer currentStudents = courseSelectionMapper.getCurrentStudentsByAssignmentId(assignmentId);
            capacity = maxStudents - currentStudents;
            redisUtil.set(capacityKey, capacity, 1, TimeUnit.HOURS);
        }
        
        return capacity;
    }
}