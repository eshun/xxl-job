package com.xxl.job.core.handler.annotation;

import java.lang.annotation.*;

/**
 *
 * @author esun
 * @date
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Execute {

    Param param() default @Param();
}
