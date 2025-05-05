package com.yangshengzhou.luckyssms.dto;

public class RegisterResponse {
    private boolean success;
    private String message;
    String token;

    public RegisterResponse(boolean success, String message, String token) {
        this.success = success;
        this.message = message;
        this.token = token;
    }

    // Getter for success
    public boolean isSuccess() {
        return success;
    }

    // Setter for success
    public void setSuccess(boolean success) {
        this.success = success;
    }

    // Getter for message
    public String getMessage() {
        return message;
    }

    // Setter for message
    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    // Setter for message
    public void setToken(String token) {
        this.token = token;
    }

    // toString method for debugging
    @Override
    public String toString() {
        return "RegisterResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                '}';
    }
}