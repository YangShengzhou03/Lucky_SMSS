package com.yangshengzhou.luckyssms.dto;

/**
 * 客户端发过来的json的字段要和下面的字段匹配，形成映射
 * 定义了接收的json的数据结构（即有哪些字段，以及这些字段的数据类型）
 * LoginRequest 类用于封装用户登录请求的数据模型。
 * 在Spring Boot应用中，这个类通常作为DTO（数据传输对象）使用，
 * 用来在不同层之间传递数据，比如从前端接收登录信息并传递给服务层处理。
 */
public class LoginRequest {

    /**
     * 用户的手机号码。在实际应用中，这可能是用户的唯一标识符之一。
     * 注意：在真实项目中，应考虑对敏感信息如密码进行加密处理。
     */
    private String phone;

    /**
     * 用户的密码。同样地，在真实环境中，应该确保密码的安全性，
     * 比如通过哈希算法加密存储，并且在网络传输过程中使用HTTPS协议。
     */
    private String password;

    /**
     * 默认构造函数。虽然在现代Java开发中，特别是在使用Lombok等工具时，
     * 可能不需要手动编写getter和setter方法以及构造函数，
     * 但在某些情况下，框架（如Spring）需要一个无参构造函数来实例化对象。
     * 如果没有提供无参构造函数，可能会导致序列化/反序列化失败或者依赖注入问题。
     */
    public LoginRequest() {}

    /**
     * 获取用户的手机号码。
     *
     * @return 用户的手机号码。
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
     * 获取用户的密码。
     *
     * @return 用户的密码。
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置用户的密码。
     *
     * @param password 用户的密码。
     */
    public void setPassword(String password) {
        this.password = password;
    }
}