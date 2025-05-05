package com.yangshengzhou.luckyssms.dto;

public class ResetResponse {
    private boolean success;
    private String message;

    public ResetResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
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

    // toString method for debugging
    @Override
    public String toString() {
        return "RegisterResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                '}';
    }
}