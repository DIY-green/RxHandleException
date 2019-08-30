package com.cheng.rxhandle;

/**
 * @author liwangcheng
 * @date 2019-08-30 06:47.
 */
public class ApiException extends Exception {

    public int errorCode;
    public String errorMsg;

    public ApiException(int errorCode) {
        super();
        this.errorCode = errorCode;
    }

    public ApiException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.errorMsg = message;
    }

    public ApiException(Throwable cause) {
        super(cause);
    }
}
