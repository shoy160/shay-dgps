package org.shay.dgps.annotation;

import java.lang.annotation.*;

/**
 * @author shay
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Mapping {

    int[] types();

    String desc() default "";
}