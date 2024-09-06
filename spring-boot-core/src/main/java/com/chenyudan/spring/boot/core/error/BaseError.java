package com.chenyudan.spring.boot.core.error;

import com.chenyudan.spring.boot.domain.enums.IError;

/**
 * Description: TODO
 *
 * @author chenyu
 * @since 2024/9/6 15:20
 */
public enum BaseError implements IError {
    CONNECT_ERROR(10010,"网络连接错误")
    ;

    private final Integer errorCode;
    private final String errorMsg;

    BaseError(Integer errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    @Override
    public Integer getErrorCode() {
        return errorCode;
    }

    @Override
    public String getErrorMsg() {
        return errorMsg;
    }
}
