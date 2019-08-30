package com.cheng.rxhandle;

/**
 * @author liwangcheng
 * @date 2019-08-30 08:23.
 */
public class Result<T> {
    public boolean succeed;
    public T result;
    public ApiException exception;

    public Result(boolean succeed,T result) {
        this.succeed = succeed;
        this.result = result;
    }

    public static Result<Integer> success(Integer orderVersion) {
        return new Result<>(true, orderVersion);
    }

    public static Result<Integer> failure(Integer orderVersion) {
        return new Result<>(false, orderVersion);
    }

    @Override
    public String toString() {
        return "{\"Result\":{"
                + "\"succeed\":\"" + succeed + "\""
                + ", \"result\":" + result
                + "}}";
    }
}
