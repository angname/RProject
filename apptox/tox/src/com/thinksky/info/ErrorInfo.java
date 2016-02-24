package com.thinksky.info;

/**
 * Created by Administrator on 2015/7/30 0030.
 */
public class ErrorInfo {

    /**
     * message : 回复不存在
     * error_code : 800
     * success : false
     */
    private String message;
    private int error_code;
    private boolean success;

    public void setMessage(String message) {
        this.message = message;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public int getError_code() {
        return error_code;
    }

    public boolean isSuccess() {
        return success;
    }
}
