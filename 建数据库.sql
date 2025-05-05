-- 创建数据库 lucky_smss
CREATE DATABASE IF NOT EXISTS lucky_smss;

-- 使用数据库 lucky_smss
USE lucky_smss;

-- 创建用户表
CREATE TABLE users (
    -- 用户ID（主键）
    uid INT NOT NULL AUTO_INCREMENT COMMENT '用户ID（主键）',
    -- 密码哈希值
    password VARCHAR(255) DEFAULT NULL COMMENT '密码哈希值',
    -- 用户角色：管理员/学生/教师，默认"student"
    role ENUM('admin', 'student', 'teacher') NOT NULL DEFAULT 'student' COMMENT '用户角色：管理员/学生/教师，默认"student"',
    -- 手机号码，唯一
    phone VARCHAR(20) DEFAULT NULL COMMENT '手机号码，唯一',
    -- 电子邮箱，唯一
    email VARCHAR(100) DEFAULT NULL COMMENT '电子邮箱，唯一',
    -- 用户头像
    avatarUrl VARCHAR(1024) DEFAULT NULL COMMENT '用户头像',
    -- 创建时间
    createdAt DATETIME DEFAULT NULL COMMENT '创建时间',
    -- 最后修改时间
    updatedAt DATETIME DEFAULT NULL COMMENT '最后修改时间',
    -- 最近登录时间
    lastLogin DATETIME DEFAULT NULL COMMENT '最近登录时间',
    -- 账号状态：启用/已禁用，默认 "1启用"
    status TINYINT NOT NULL DEFAULT 1 COMMENT '账号状态：启用/已禁用，默认 "1启用"',
    PRIMARY KEY (uid),
    UNIQUE KEY unique_phone (phone),
    UNIQUE KEY unique_email (email)
) COMMENT='用户信息表';

-- 创建学生表
CREATE TABLE students (
    -- 学生唯一标识符，同时也是外键
    uid INT NOT NULL COMMENT '学生唯一标识符，同时也是外键',
    -- 学生姓名
    name VARCHAR(255) DEFAULT NULL COMMENT '学生姓名',
    -- 性别（Male: 男, Female: 女）
    gender ENUM('Male', 'Female') DEFAULT NULL COMMENT '性别（Male: 男, Female: 女）',
    -- 出生日期
    birthDate DATE DEFAULT NULL COMMENT '出生日期',
    -- 入学日期
    enrollmentDate DATE DEFAULT NULL COMMENT '入学日期',
    -- 所在班级名称
    class VARCHAR(255) DEFAULT NULL COMMENT '所在班级名称',
    -- 所学专业名称
    major VARCHAR(255) DEFAULT NULL COMMENT '所学专业名称',
    -- 所属学院名称
    college VARCHAR(255) DEFAULT NULL COMMENT '所属学院名称',
    -- 家庭住址
    address VARCHAR(255) DEFAULT NULL COMMENT '家庭住址',
    -- 学籍状态（在读、休学、毕业、退学）
    studentStatus ENUM('在读', '休学', '毕业', '退学') DEFAULT NULL COMMENT '学籍状态（在读、休学、毕业、退学）',
    -- 已修学分
    creditsCompleted INT DEFAULT NULL COMMENT '已修学分',
    -- 要求总学分
    creditsRequired INT DEFAULT NULL COMMENT '要求总学分',
    PRIMARY KEY (uid),
    -- 定义外键约束
    CONSTRAINT fk_students_uid FOREIGN KEY (uid) REFERENCES users(uid) ON DELETE CASCADE ON UPDATE CASCADE
) COMMENT='学生信息表';

-- 创建教师表
CREATE TABLE teachers (
    -- 用户ID（主键），外键关联到users表
    uid INT NOT NULL COMMENT '用户ID（主键），外键关联到users表',
    -- 姓名
    name VARCHAR(255) DEFAULT NULL COMMENT '姓名',
    -- 性别（Male: 男, Female: 女）
    gender ENUM('Male', 'Female') DEFAULT NULL COMMENT '性别（Male: 男, Female: 女）',
    -- 职称，如教授、副教授等
    title VARCHAR(100) DEFAULT NULL COMMENT '职称，如教授、副教授等',
    -- 入岗时间
    hireDate DATE DEFAULT NULL COMMENT '入岗时间',
    -- 所属系别
    department VARCHAR(255) DEFAULT NULL COMMENT '所属系别',
    -- 研究兴趣或领域
    researchInterests TEXT DEFAULT NULL COMMENT '研究兴趣或领域',
    -- 学历背景，如博士、硕士等
    educationBackground VARCHAR(255) DEFAULT NULL COMMENT '学历背景，如博士、硕士等',
    -- 毕业院校
    graduatedFrom VARCHAR(255) DEFAULT NULL COMMENT '毕业院校',
    PRIMARY KEY (uid),
    -- 定义外键约束
    CONSTRAINT fk_teachers_uid FOREIGN KEY (uid) REFERENCES users(uid) ON DELETE CASCADE ON UPDATE CASCADE
) COMMENT='教师信息表';

DELIMITER $$

-- 创建触发器，当用户角色更改时同步更新students和teachers表
CREATE TRIGGER after_users_role_update 
AFTER UPDATE ON users
FOR EACH ROW
BEGIN
    -- 如果角色从学生变为其他
    IF OLD.role = 'student' AND NEW.role != 'student' THEN
        DELETE FROM students WHERE uid = OLD.uid;
    END IF;

    -- 如果角色从老师变为其他
    IF OLD.role = 'teacher' AND NEW.role != 'teacher' THEN
        DELETE FROM teachers WHERE uid = OLD.uid;
    END IF;

    -- 如果角色变更为学生
    IF NEW.role = 'student' AND OLD.role != 'student' THEN
        INSERT INTO students (uid) VALUES (NEW.uid);
    END IF;

    -- 如果角色变更为老师
    IF NEW.role = 'teacher' AND OLD.role != 'teacher' THEN
        INSERT INTO teachers (uid) VALUES (NEW.uid);
    END IF;
END$$

DELIMITER ;