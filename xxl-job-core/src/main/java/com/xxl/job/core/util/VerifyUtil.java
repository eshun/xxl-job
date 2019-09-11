package com.xxl.job.core.util;

import java.util.Collection;
import java.util.Map;

/**
 * TODO
 *
 * @author esun
 * @version v1.0
 * @date: 2019/9/11
 */
public class VerifyUtil {

    public static boolean isNullOrEmpty(Object obj) {
        try {
            if (obj == null){
                return true;
            }
            if (obj instanceof String) {
                return "".equals(obj);
            }
            if (obj instanceof CharSequence) {
                return ((CharSequence) obj).length() == 0;
            }
            if (obj instanceof Collection) {
                return ((Collection<?>) obj).isEmpty();
            }
            if (obj instanceof Map) {
                return ((Map<?, ?>) obj).isEmpty();
            }
            if (obj instanceof Object[]) {
                Object[] object = (Object[]) obj;
                if (object.length == 0) {
                    return true;
                }
                boolean empty = true;
                for (int i = 0; i < object.length; i++) {
                    if (!isNullOrEmpty(object[i])) {
                        empty = false;
                        break;
                    }
                }
                return empty;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }

    }

    public static boolean isNumeric(String str){
        try {
            int result = Integer.valueOf(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
