package com.cheng.rxhandle;

/**
 * @author liwangcheng
 * @date 2019-08-30 07:04.
 */
public class UpdateVersionException extends ApiException {

    public int orderVersion;

    public UpdateVersionException(int errorCode, String message) {
        super(errorCode, message);
        orderVersion = errorCode;
        errorMsg = message;
    }

    public UpdateVersionException(int errorCode, Throwable tr) {
        super(errorCode);
        orderVersion = errorCode;
        if (tr instanceof ApiException) {
            errorMsg = ((ApiException) tr).errorMsg;
        }
    }
}
