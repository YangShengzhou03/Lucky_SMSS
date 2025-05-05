package com.yangshengzhou.luckyssms.dto;

public class RegisterRequest {
    private int uid;
    private String phone;
    private int verificationCode;

    // Getter for uid
    public int getUid() {
        return uid;
    }

    // Setter for uid
    public void setUid(int uid) {
        this.uid = uid;
    }

    // Getter for phone
    public String getPhone() {
        return phone;
    }

    // Setter for phone
    public void setPhone(String phone) {
        this.phone = phone;
    }

    // Getter for verificationCode
    public int getVerificationCode() {
        return verificationCode;
    }

    // Setter for verificationCode
    public void setVerificationCode(int verificationCode) {
        this.verificationCode = verificationCode;
    }
}