package com.chenyudan.spring.boot.core.utils;

import com.chenyudan.spring.boot.domain.enums.IError;
import com.chenyudan.spring.boot.domain.exception.BaseException;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collection;

/**
 * Description: TODO
 *
 * @author chenyu
 * @since 2022/3/27 21:49
 */
public class AssertUtil {

    public static void isTrue(boolean b, IError error, Object... objects) {
        if (!b) {
            throw new BaseException(error, objects);
        }
    }

    public static void notNull(Object obj, IError error, Object... objects) {
        if (obj == null) {
            throw new BaseException(error, objects);
        }
    }

    public static <T> void notEmpty(Collection<?> collection, IError error, Object... objects) {
        if (CollectionUtils.isEmpty(collection)) {
            throw new BaseException(error, objects);
        }
    }
}
