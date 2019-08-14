package com.xxl.job.core.util;

import java.io.ObjectStreamClass;
import java.util.Arrays;

/**
 * TODO
 *
 * @author esun
 * @version v1.0
 * @date: 2019-08-13
*/
public class ReflectionUtil {
    private final static String[] TYPES = {"java.lang.Integer",
            "java.lang.Double",
            "java.lang.Float",
            "java.lang.Long",
            "java.lang.Short",
            "java.lang.Byte",
            "java.lang.Boolean",
            "java.lang.Character",
            "java.lang.String",
            "int","double","long","short","byte","boolean","char","float"};

    public static Long getSerialVersionUID(Class<?> cl) {
        try {
            ObjectStreamClass objectStreamClass = ObjectStreamClass.lookup(cl);
            return objectStreamClass.getSerialVersionUID();
        } catch (Exception e) {
            return null;
        }
    }
    public static Class<?> getObjectCla(Class<?> cla) {
        if (String.class.isAssignableFrom(cla)) {
            return String.class;
        } else if (Integer.class.isAssignableFrom(cla)) {
            return Integer.class;
        } else if (Long.class.isAssignableFrom(cla)) {
            return Long.class;
        } else if (Double.class.isAssignableFrom(cla)) {
            return Double.class;
        } else if (Float.class.isAssignableFrom(cla)) {
            return Float.class;
        } else if (Boolean.class.isAssignableFrom(cla)) {
            return Boolean.class;
        } else if (Byte.class.isAssignableFrom(cla)) {
            return Byte.class;
        } else {
            return cla;
        }
    }

    public static boolean isPrimitive(String clzName) {
        try {
            boolean isPrimitive = Arrays.asList(TYPES).contains(clzName);
            return isPrimitive;
        } catch (Exception e) {
            return false;
        }
    }
}
