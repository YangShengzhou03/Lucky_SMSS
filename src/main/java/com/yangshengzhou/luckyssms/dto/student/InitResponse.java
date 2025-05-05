package com.yangshengzhou.luckyssms.dto.student;

import java.util.Objects;

public class InitResponse {
    private final String avatarUrl;
    private final String name;
    private final int uid;
    private final String phone;
    private final String email;
    private final String address;
    private final String college;
    private final String major;
    private final String enrollmentDate;
    private final int creditsCompleted;
    private final int creditsRequired;

    // 私有构造方法，仅由 Builder 使用
    private InitResponse(Builder builder) {
        this.avatarUrl = builder.avatarUrl;
        this.name = builder.name;
        this.uid = builder.uid;
        this.phone = builder.phone;
        this.email = builder.email;
        this.address = builder.address;
        this.college = builder.college;
        this.major = builder.major;
        this.enrollmentDate = builder.enrollmentDate;
        this.creditsCompleted = builder.creditsCompleted;
        this.creditsRequired = builder.creditsRequired;
    }

    // Getter 方法
    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getName() {
        return name;
    }

    public int getUid() {
        return uid;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public String getCollege() { // 修正方法名
        return college;
    }

    public String getMajor() {
        return major;
    }

    public String getEnrollmentDate() {
        return enrollmentDate;
    }

    public int getCreditsCompleted() {
        return creditsCompleted;
    }

    public int getCreditsRequired() {
        return creditsRequired;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InitResponse that = (InitResponse) o;
        return uid == that.uid &&
                creditsCompleted == that.creditsCompleted &&
                creditsRequired == that.creditsRequired &&
                Objects.equals(avatarUrl, that.avatarUrl) &&
                Objects.equals(name, that.name) &&
                Objects.equals(phone, that.phone) &&
                Objects.equals(email, that.email) &&
                Objects.equals(address, that.address) &&
                Objects.equals(college, that.college) &&
                Objects.equals(major, that.major) &&
                Objects.equals(enrollmentDate, that.enrollmentDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(avatarUrl, name, uid, phone, email, address, college, major, enrollmentDate, creditsCompleted, creditsRequired);
    }

    @Override
    public String toString() {
        return "InitResponse{" +
                "avatarUrl='" + avatarUrl + '\'' +
                ", name='" + name + '\'' +
                ", uid=" + uid +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", college='" + college + '\'' +
                ", major='" + major + '\'' +
                ", enrollmentDate='" + enrollmentDate + '\'' +
                ", creditsCompleted=" + creditsCompleted +
                ", creditsRequired=" + creditsRequired +
                '}';
    }

    // Builder 类
    public static class Builder {
        private String avatarUrl;
        private String name;
        private int uid;
        private String phone;
        private String email;
        private String address;
        private String college;
        private String major;
        private String enrollmentDate;
        private int creditsCompleted;
        private int creditsRequired;

        public Builder avatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder uid(int uid) {
            this.uid = uid;
            return this;
        }

        public Builder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder address(String address) {
            this.address = address;
            return this;
        }

        public Builder college(String college) {
            this.college = college;
            return this;
        }

        public Builder major(String major) {
            this.major = major;
            return this;
        }

        public Builder enrollmentDate(String enrollmentDate) {
            this.enrollmentDate = enrollmentDate;
            return this;
        }

        public Builder creditsCompleted(int creditsCompleted) {
            this.creditsCompleted = creditsCompleted;
            return this;
        }

        public Builder creditsRequired(int creditsRequired) {
            this.creditsRequired = creditsRequired;
            return this;
        }

        public InitResponse build() {
            return new InitResponse(this);
        }
    }
}