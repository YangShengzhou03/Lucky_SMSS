package com.yangshengzhou.luckyssms.dto.student;

public class UpdateResponse {
    private boolean success;
    private String message;

    public UpdateResponse(boolean success, String message) {
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
        return "UpdateResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                '}';
    }
}