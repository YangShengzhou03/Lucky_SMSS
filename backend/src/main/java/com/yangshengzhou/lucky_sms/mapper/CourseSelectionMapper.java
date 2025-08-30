package com.yangshengzhou.lucky_sms.mapper;

import com.yangshengzhou.lucky_sms.entity.CourseSelection;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CourseSelectionMapper {

    /**
     * 插入选课记录
     */
    int insert(CourseSelection selection);

    /**
     * 根据学生ID和授课ID查询选课记录
     */
    CourseSelection findByStudentAndAssignment(@Param("studentId") Integer studentId, 
                                              @Param("assignmentId") Integer assignmentId);

    /**
     * 更新选课状态
     */
    int updateStatus(@Param("selectionId") Integer selectionId, 
                    @Param("status") String status);

    /**
     * 根据授课ID获取最大选课人数
     */
    Integer getMaxStudentsByAssignmentId(@Param("assignmentId") Integer assignmentId);

    /**
     * 根据授课ID获取当前选课人数
     */
    Integer getCurrentStudentsByAssignmentId(@Param("assignmentId") Integer assignmentId);

    /**
     * 增加当前选课人数
     */
    int incrementCurrentStudents(@Param("assignmentId") Integer assignmentId);

    /**
     * 减少当前选课人数
     */
    int decrementCurrentStudents(@Param("assignmentId") Integer assignmentId);
}