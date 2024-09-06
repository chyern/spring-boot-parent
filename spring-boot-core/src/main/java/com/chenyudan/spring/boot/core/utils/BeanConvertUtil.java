package com.chenyudan.spring.boot.core.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.beanutils.BeanUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Description: 对象转换工具类
 *
 * @author chenyu
 * @see org.apache.commons.beanutils.BeanUtils
 * @since 2021/4/20
 */
@Slf4j
public class BeanConvertUtil {

    public static <T> T convert(Object org, Class<T> clazz) {
        T dest = null;
        try {
            dest = clazz.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(org, dest);
        } catch (Exception e) {
            log.error("{} to class {} error", org.getClass().getName(), clazz.getName(), e);
        }
        return dest;
    }

    public static <T> List<T> convert(List<?> org, Class<T> clazz) {
        List<T> dest = new ArrayList<>();
        org.forEach(item -> dest.add(convert(item, clazz)));
        return dest;
    }

    public static Map<String, Object> beanToMap(Object obj) {
        Map<String, Object> map = new HashMap<>();
        for (Entry<Object, Object> entry : new BeanMap(obj).entrySet()) {
            String key = entry.getKey().toString();
            if ("class".equals(key)) {
                continue;
            }
            map.put(key, entry.getValue());
        }
        return map;
    }
}
