package com.chenyudan.spring.boot.core.error;

import com.chenyudan.spring.boot.domain.enums.IError;

/**
 * Description: TODO
 *
 * @author chenyu
 * @since 2024/9/6 15:20
 */
public enum BaseError implements IError {
    CONNECT_ERROR(10010, "网络连接错误"),
    FILE_EXIST(100101, "文件已存在"),
    FILE_NOT_FIND(100102, "文件不存在"),
    FILE_NOT_IS_DIRECTORY(100103, "文件不是一个目录"),
    DIRECTORY_IS_EMPTY(100104, "目录是空的"),
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
