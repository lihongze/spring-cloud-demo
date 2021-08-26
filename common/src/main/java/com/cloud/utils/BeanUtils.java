package com.cloud.utils;

import com.google.common.collect.Maps;
import org.springframework.cglib.beans.BeanMap;

import java.util.Map;

/**
 * @author hongze
 * @date
 * @apiNote
 */
public class BeanUtils {
    /**
     * 将对象装换为map
     *
     * @param bean
     * @return
     */
    public static <T> Map<String, Object> beanToMap(T bean) {
        Map<String, Object> map = Maps.newHashMap();
        if (bean != null) {
            BeanMap beanMap = BeanMap.create(bean);
            for (Object key : beanMap.keySet()) {
                if (EmptyUtils.isEmpty(beanMap.get(key))) {
                    map.put(key + "", "");
                } else {
                    map.put(key + "", beanMap.get(key));
                }
            }
        }
        return map;
    }
}
