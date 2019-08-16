package com.xxl.job.core.util;

import com.xxl.job.core.handler.annotation.Execute;
import com.xxl.job.core.handler.annotation.Param;
import org.springframework.util.StringUtils;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.*;
import java.util.*;

/**
 * TODO
 *
 * @author esun
 * @version v1.0
 * @date: 2019-08-15
 */
public class PojoToJsonUtil {

    public static Object pojoToJson(Type type, Execute execute,Field field) {
        if(type==null){
            return null;
        }
        Class<?> clazz = ParameterizedType.class.isAssignableFrom(type.getClass()) ?
                ((ParameterizedTypeImpl) type).getRawType() : (Class<?>) type;
        if (clazz == null || clazz == void.class || clazz == Void.class) {
            return null;
        } else if (clazz.isArray()) {
            Map<String, Object> map = ReflectionUtil.getArrayClass(clazz, 1);
            int len = (int) map.get("length");
            clazz = (Class<?>) map.get("clazz");
            return Array.newInstance(clazz, len);

        } else if (ReflectionUtil.isPrimitive(clazz)) {
            if (execute != null) {
                Param param = execute.param();
                if (StringUtils.hasText(param.defaultValue())) {
                    return param.defaultValue();
                }
            }
            if (field != null) {
                Param paramAnnotation = field.getAnnotation(Param.class);
                if (paramAnnotation != null && StringUtils.hasText(paramAnnotation.defaultValue())) {
                    return paramAnnotation.defaultValue();
                }
            }
            return ReflectionUtil.getDefaultValue(clazz);
        } else if (ReflectionUtil.isPojo(clazz)) {
            try {
                Field[] fields = clazz.getDeclaredFields();
                Object instance = clazz.newInstance();
                try{
                    for (Field f : fields) {
                        int modifiers = f.getModifiers();
                        Type genericType = f.getGenericType();
                        Class<?> fieldClass = f.getType();
                        if (genericType == null || Modifier.isStatic(modifiers) || Modifier.isInterface(modifiers) || Modifier.isAbstract(modifiers)) {
                            continue;
                        }
                        Method method = ReflectionUtil.getBeanPublicSetterMethod(clazz, f.getName(), fieldClass);
                        if (method != null) {
                            if (!method.isAccessible()) {
                                method.setAccessible(true);
                            }
                            try {
                                if (genericType.getTypeName().equals("T") && type instanceof ParameterizedType) {
                                    ParameterizedType parameterizedType = (ParameterizedType) type;
                                    fieldClass = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                                }
                                Object value = pojoToJson(fieldClass, null, f);
                                method.invoke(instance, value);
                            } catch (Exception e) {

                            }

                        }
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }
                return instance;
            } catch (Exception e) {

            }
        } else if (Collection.class.isAssignableFrom(clazz)) {
            Collection<Object> objects = new ArrayList<Object>();
            type = ReflectionUtil.getGenericClassByIndex(type, 0);
            if(type!=null){
                Object object = pojoToJson(type, execute, null);
                if (HashSet.class.isAssignableFrom(clazz)) {
                    return new HashSet<Object>();
                } else if (!clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers())) {
                    try {
                        objects = (Collection<Object>) clazz.newInstance();
                    } catch (Exception e) {
                        // ignore
                    }
                }
                objects.add(object);
            }
            return objects;
        } else if (Map.class.isAssignableFrom(clazz)) {
            Map<Object, Object> objectMap = new HashMap<Object, Object>();
            Type type1 = ReflectionUtil.getGenericClassByIndex(type, 0);
            Type type2 = ReflectionUtil.getGenericClassByIndex(type, 1);
            if(type1!=null&&type2!=null) {
                Object object1 = pojoToJson(type1, execute, null);
                Object object2 = pojoToJson(type2, execute, null);
                objectMap.put(object1, object2);
            }
            return objectMap;
        }
        return null;
    }

}
