package com.chain.mvcframework.annotation;

import java.lang.annotation.*;

/**
 * @author chenqian091
 * @date 2020-08-16
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CHController {
    String value() default "";
}
