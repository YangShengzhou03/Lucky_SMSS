package com.yangshengzhou.luckyssms.model;

public class Student {
    private String avatarUrl;
    private int uid;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String college;
    private String major;
    private String enrollmentDate;
    private int creditsCompleted;
    private int creditsRequired;

    // Getters
    public String getAvatarUrl() {
        return avatarUrl;
    }

    public int getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getCollege() {
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

    // Setters
    public void setAvatarUrl(String avatar_url) {
        this.avatarUrl = avatar_url;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public void setEnrollmentDate(String enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public void setCreditsCompleted(int creditsCompleted) {
        this.creditsCompleted = creditsCompleted;
    }

    public void setCreditsRequired(int creditsRequired) {
        this.creditsRequired = creditsRequired;
    }
}