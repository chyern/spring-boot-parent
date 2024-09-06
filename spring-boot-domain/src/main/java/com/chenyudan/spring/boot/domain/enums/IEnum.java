package com.chenyudan.spring.boot.domain.enums;

/**
 * Description: TODO
 *
 * @author chenyu
 * @since 2022/7/28 12:02
 */
public interface IEnum<T> {

    T getDefinition();

    String getDesc();


}
