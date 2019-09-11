package com.xxl.job.core.util;

import java.io.ObjectStreamClass;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.Future;

/**
 * TODO
 *
 * @author esun
 * @version v1.0
 * @date: 2019-08-13
*/
public class ReflectionUtil {

    public static Class<?> loadClassByName(String className) throws ClassNotFoundException {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            return Thread.currentThread().getContextClassLoader().loadClass(className);
        }
    }

    public static Long getSerialVersionUID(Class<?> cl) {
        try {
            ObjectStreamClass objectStreamClass = ObjectStreamClass.lookup(cl);
            return objectStreamClass.getSerialVersionUID();
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean isBoolean(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }
        return clazz == boolean.class || clazz == Boolean.class;
    }

    public static boolean isByte(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }
        return clazz == byte.class || clazz == Byte.class;
    }

    public static boolean isInteger(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }
        return clazz == int.class || clazz == Integer.class;
    }

    public static boolean isDouble(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }
        return clazz == double.class || clazz == Double.class;
    }

    public static boolean isLong(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }
        return clazz == long.class || clazz == Long.class;
    }

    public static boolean isFloat(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }
        return clazz == float.class || clazz == Float.class;
    }

    public static boolean isShort(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }
        return clazz == short.class || clazz == Short.class;
    }

    public static boolean isString(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }
        return clazz == String.class;
    }

    public static boolean isCharacter(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }
        return clazz == char.class || clazz == Float.class;
    }

    public static boolean isPrimitive(Class<?> clazz) {
        return clazz.isPrimitive() || isBoolean(clazz) || isString(clazz) || isCharacter(clazz) ||
                Number.class.isAssignableFrom(clazz) || Date.class.isAssignableFrom(clazz);
    }

    public static boolean isPojo(Class<?> clazz) {
        return !isPrimitive(clazz) && !clazz.isInterface()
                && !Collection.class.isAssignableFrom(clazz)
                && !Map.class.isAssignableFrom(clazz);
    }

    public static Object getDefaultValue(Class<?> clazz) {
        if (isCharacter(clazz)) {
            return Character.MIN_VALUE;
        } else if (isBoolean(clazz)) {
            return false;
        } else if (isShort(clazz)) {
            return (short) 0;
        } else if (isByte(clazz)) {
            return (byte) 0;
        } else if (isFloat(clazz)) {
            return 0F;
        } else if (isLong(clazz)) {
            return 0L;
        } else if (isDouble(clazz)) {
            return 0D;
        } else if (isString(clazz)) {
            return "";
        } else {
            return clazz.isPrimitive() ? 0 : null;
        }
    }

    public static Map<String, Object> getArrayClass(Class<?> clazz, int len) {
        Map<String, Object> map = new HashMap<>(2);
        if (clazz.isArray()) {
            return getArrayClass(clazz.getComponentType(), len + 1);
        } else {
            map.put("clazz", clazz);
            map.put("length", len);
            return map;
        }
    }

    public static boolean isBeanPropertyReadMethod(Method method) {
        return method != null
                && Modifier.isPublic(method.getModifiers())
                && !Modifier.isStatic(method.getModifiers())
                && method.getReturnType() != void.class
                && method.getDeclaringClass() != Object.class
                && method.getParameterTypes().length == 0
                && ((method.getName().startsWith("get") && method.getName().length() > 3)
                || (method.getName().startsWith("is") && method.getName().length() > 2));
    }

    public static String getPropertyNameFromBeanReadMethod(Method method) {
        if (isBeanPropertyReadMethod(method)) {
            if (method.getName().startsWith("get")) {
                return method.getName().substring(3, 4).toLowerCase()
                        + method.getName().substring(4);
            }
            if (method.getName().startsWith("is")) {
                return method.getName().substring(2, 3).toLowerCase()
                        + method.getName().substring(3);
            }
        }
        return null;
    }

    public static boolean isBeanPropertyWriteMethod(Method method) {
        return method != null
                && Modifier.isPublic(method.getModifiers())
                && !Modifier.isStatic(method.getModifiers())
                && method.getDeclaringClass() != Object.class
                && method.getParameterTypes().length == 1
                && method.getName().startsWith("set")
                && method.getName().length() > 3;
    }

    public static String getPropertyNameFromBeanWriteMethod(Method method) {
        if (isBeanPropertyWriteMethod(method)) {
            return method.getName().substring(3, 4).toLowerCase()
                    + method.getName().substring(4);
        }
        return null;
    }

    public static boolean isPublicInstanceField(Field field) {
        return Modifier.isPublic(field.getModifiers())
                && !Modifier.isStatic(field.getModifiers())
                && !Modifier.isFinal(field.getModifiers())
                && !field.isSynthetic();
    }

    public static Map<String, Field> getBeanPropertyFields(Class cl) {
        Map<String, Field> properties = new HashMap<String, Field>();
        for (; cl != null; cl = cl.getSuperclass()) {
            Field[] fields = cl.getDeclaredFields();
            for (Field field : fields) {
                if (Modifier.isTransient(field.getModifiers())
                        || Modifier.isStatic(field.getModifiers())) {
                    continue;
                }

                field.setAccessible(true);

                properties.put(field.getName(), field);
            }
        }

        return properties;
    }

    public static Map<String, Method> getBeanPropertyReadMethods(Class cl) {
        Map<String, Method> properties = new HashMap<String, Method>();
        for (; cl != null; cl = cl.getSuperclass()) {
            Method[] methods = cl.getDeclaredMethods();
            for (Method method : methods) {
                if (isBeanPropertyReadMethod(method)) {
                    method.setAccessible(true);
                    String property = getPropertyNameFromBeanReadMethod(method);
                    properties.put(property, method);
                }
            }
        }

        return properties;
    }

    public static Method getBeanPublicSetterMethod(Class cl, String name, Class<?> valueCls) {
        try {
            String methodName = "set" + name.substring(0, 1).toUpperCase()
                    + name.substring(1);
            Method method = cl.getMethod(methodName, valueCls);
            if (method != null
                    && Modifier.isPublic(method.getModifiers())
                    && !Modifier.isStatic(method.getModifiers())
                    && method.getReturnType() == void.class) {
                return method;
            }
        } catch (NoSuchMethodException e) {

        }
        return null;
    }

    public static Type[] getReturnTypes(Method method) {
        Class<?> returnType = method.getReturnType();
        Type genericReturnType = method.getGenericReturnType();
        if (Future.class.isAssignableFrom(returnType)) {
            if (genericReturnType instanceof ParameterizedType) {
                Type actualArgType = ((ParameterizedType) genericReturnType).getActualTypeArguments()[0];
                if (actualArgType instanceof ParameterizedType) {
                    returnType = (Class<?>) ((ParameterizedType) actualArgType).getRawType();
                    genericReturnType = actualArgType;
                } else {
                    returnType = (Class<?>) actualArgType;
                    genericReturnType = returnType;
                }
            } else {
                returnType = null;
                genericReturnType = null;
            }
        }
        return new Type[]{returnType, genericReturnType};
    }

    public static Type getGenericClassByIndex(Type genericType, int index) {
        Type clazz = null;
        // find parameterized type
        if (genericType instanceof ParameterizedType) {
            ParameterizedType t = (ParameterizedType) genericType;
            Type[] types = t.getActualTypeArguments();
            clazz = types[index];
        }
        return clazz;
    }
}
