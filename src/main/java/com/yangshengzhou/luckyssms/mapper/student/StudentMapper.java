package com.yangshengzhou.luckyssms.mapper.student;

import com.yangshengzhou.luckyssms.model.Student;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface StudentMapper {

    @Select("SELECT u.uid, s.name, u.email, u.phone, u.avatarUrl, s.address, s.college, s.major, s.enrollmentDate, s.creditsRequired, s.creditsCompleted " +
            "FROM users u INNER JOIN students s ON u.uid = s.uid WHERE u.uid = #{uid}")
    Student findByUid(@Param("uid") int uid);

    // 更新头像
    @Update("UPDATE users SET avatarUrl = #{avatarUrl} WHERE uid = #{uid}")
    void updateAvatar(@Param("uid") int uid, @Param("avatarUrl") String avatarUrl);

    /**
     * 更新学生信息，同时更新 students 和 users 表。
     */
    @Update("UPDATE students SET name = #{name}, gender = #{gender}, birthdate = #{birthDate}, address = #{address} WHERE uid = #{uid}")
    void updateStudentInfo(@Param("uid") int uid,
                           @Param("name") String name,
                           @Param("gender") String gender,
                           @Param("birthDate") String birthDate,
                           @Param("address") String address);

    @Update("UPDATE users SET phone = #{phone}, email = #{email} WHERE uid = #{uid}")
    void updateUserContact(@Param("uid") int uid,
                           @Param("phone") String phone,
                           @Param("email") String email);
}