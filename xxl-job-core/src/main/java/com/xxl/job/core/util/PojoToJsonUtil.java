package com.xxl.job.core.util;

import com.xxl.job.core.handler.annotation.Execute;
import com.xxl.job.core.handler.annotation.Param;
import org.springframework.util.StringUtils;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * TODO
 *
 * @author esun
 * @version v1.0
 * @date: 2019-08-15
 * @update [序号][日期YYYY-MM-DD] [更改人姓名][变更描述]
 */
public class PojoToJsonUtil {

    public static String pojoToJson(Type type, Execute execute) {
        Class<?> clazz = ParameterizedType.class.isAssignableFrom(type.getClass()) ?
                ((ParameterizedTypeImpl) type).getRawType() : (Class<?>) type;
        if (clazz == null || clazz == void.class || clazz == Void.class) {
            return null;
        } else if (clazz.isArray()) {
            int l = ReflectionUtil.getArrayLenght(clazz, 1);

        } else if (ReflectionUtil.isPrimitive(clazz)) {
            if (execute != null) {
                Param param = execute.param();
                if (StringUtils.hasText(param.defaultValue())) {
                    return param.defaultValue();
                }
            }
            return ReflectionUtil.getDefaultValue(clazz).toString();
        } else if (ReflectionUtil.isPojo(clazz)) {

        } else if (Collection.class.isAssignableFrom(clazz)) {

        } else if (Map.class.isAssignableFrom(clazz)) {

        }
        return null;
    }

}
