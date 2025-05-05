package com.yangshengzhou.luckyssms.dto.student;

/**
 * 用于封装更新头像操作的结果。
 */
public class UpdateAvatarResponse {

    private boolean success;
    private String message;

    /**
     * 构造函数。
     *
     * @param success 操作是否成功
     * @param message 返回的消息
     */
    public UpdateAvatarResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    /**
     * 获取操作是否成功的标志。
     *
     * @return 操作是否成功
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * 设置操作是否成功的标志。
     *
     * @param success 操作是否成功
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * 获取返回的消息。
     *
     * @return 返回的消息
     */
    public String getMessage() {
        return message;
    }

    /**
     * 设置返回的消息。
     *
     * @param message 返回的消息
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 覆盖toString方法，便于调试时输出对象信息。
     *
     * @return 对象的字符串表示
     */
    @Override
    public String toString() {
        return "UpdateAvatarResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                '}';
    }
}