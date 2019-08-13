package com.xxl.job.core.util;

import java.io.ObjectStreamClass;

/**
 * TODO
 *
 * @author esun
 * @version v1.0
 * @date: 2019-08-13
*/
public class ReflectionUtil {

    public static Long getSerialVersionUID(Class<?> cl) {
        try {
            ObjectStreamClass objectStreamClass = ObjectStreamClass.lookup(cl);
            return objectStreamClass.getSerialVersionUID();
        } catch (Exception e) {
            return null;
        }
    }
}
