package com.chenyudan.spring.boot.domain.exception;

import com.chenyudan.spring.boot.domain.enums.IError;
import lombok.Getter;

/**
 * Description: TODO
 *
 * @author chenyu
 * @since 2024/9/6
 */
public class BaseException extends RuntimeException {

    @Getter
    private final IError error;

    public BaseException(IError error) {
        super(error.toString());
        this.error = error;
    }

}
