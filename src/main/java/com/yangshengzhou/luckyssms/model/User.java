package com.yangshengzhou.luckyssms.model;

/**
 * User 类用于表示用户信息的数据模型。形成和数据库表的映射，需要一一对应。
 * 包含用户的唯一标识符、手机号码、角色以及密码等属性。
 */
public class User {
    /**
     * 用户的唯一标识符。
     */
    private int uid;

    /**
     * 用户的手机号码。
     */
    private String phone;

    /**
     * 用户的角色，例如："student", "teacher", 或 "admin"。
     */
    private String role;

    /**
     * 用户的登录密码。
     * 注意：在实际应用中，应确保对密码进行适当的加密处理。
     */
    private String password;

    // 默认构造函数
    public User() {}

    // 带参数的构造函数（可选）
    public User(int uid, String phone, String role, String password) {
        this.uid = uid;
        this.phone = phone;
        this.role = role;
        this.password = password;
    }

    // Getter 和 Setter 方法

    /**
     * 获取用户的唯一标识符。
     *
     * @return 返回用户的UID。
     */
    public int getUid() {
        return uid;
    }

    /**
     * 设置用户的唯一标识符。
     *
     * @param uid 用户的UID。
     */
    public void setUid(int uid) {
        this.uid = uid;
    }

    /**
     * 获取用户的手机号码。
     *
     * @return 返回用户的手机号码。
     */
    public String getPhone() {
        return phone;
    }

    /**
     * 设置用户的手机号码。
     *
     * @param phone 用户的手机号码。
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * 获取用户的角色。
     *
     * @return 返回用户的角色。
     */
    public String getRole() {
        return role;
    }

    /**
     * 设置用户的角色。
     *
     * @param role 用户的角色。
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * 获取用户的登录密码。
     *
     * @return 返回用户的密码。
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置用户的登录密码。
     *
     * @param password 用户的密码。
     */
    public void setPassword(String password) {
        this.password = password;
    }
}