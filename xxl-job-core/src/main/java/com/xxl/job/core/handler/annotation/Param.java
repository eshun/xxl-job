package com.xxl.job.core.handler.annotation;

import java.lang.annotation.*;

/**
 *
 * @author esun
 * @date
 */
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Param {

    String name() default "";

    String value() default "";

    boolean required() default false;

    String defaultValue() default "";
}
