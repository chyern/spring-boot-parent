package com.chenyudan.spring.boot.domain.response;

import com.chenyudan.spring.boot.domain.enums.IError;

import java.io.Serializable;

/**
 * Description: TODO
 *
 * @author chenyu
 * @since 2024/9/6 14:28
 */
public class Response<T> implements Serializable {

    private static final long serialVersionUID = -4710192148141356262L;

    private final Boolean success;
    private final T result;
    private final Integer errorCode;
    private final String errorMsg;
    private final Long t;

    Response(T result) {
        this.success = true;
        this.result = result;
        this.errorCode = null;
        this.errorMsg = null;
        this.t = System.currentTimeMillis();
    }

    Response(Integer errorCode, String errorMsg) {
        this.success = false;
        this.result = null;
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
        this.t = System.currentTimeMillis();
    }

    public static <T> Response<T> success(T result) {
        return new Response<>(result);
    }

    public static <T> Response<T> fail(IError error) {
        Integer errorCode = error.getErrorCode();
        String errorMsg = error.getErrorMsg();
        return new Response<>(errorCode, errorMsg);
    }

    public static <T> Response<T> fail(Integer errorCode, String errorMsg) {
        return new Response<>(errorCode, errorMsg);
    }

    public Boolean getSuccess() {
        return success;
    }

    public T getResult() {
        return result;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public Long getT() {
        return t;
    }
}
