package org.shay.dgps.annotation;

import java.lang.annotation.*;

/**
 * @author shay
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Type {

    int[] value();

    String desc() default "";

}