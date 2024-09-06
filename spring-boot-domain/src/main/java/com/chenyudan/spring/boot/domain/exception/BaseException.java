package com.chenyudan.spring.boot.domain.exception;

import com.chenyudan.spring.boot.domain.enums.IError;

/**
 * Description: TODO
 *
 * @author chenyu
 * @since 2024/9/6
 */
public class BaseException extends RuntimeException {

    private final IError error;

    public IError getError() {
        return error;
    }

    public BaseException(IError error, Object... objects) {
        super(error.toString());
        this.error = error;
    }

}
