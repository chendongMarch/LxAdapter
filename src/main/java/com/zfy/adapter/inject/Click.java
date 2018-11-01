package com.zfy.adapter.inject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * CreateAt : 2018/2/24
 * Describe :
 *
 * @author chendong
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Click {

    boolean dbClick() default false;

    int[] disableTypes() default {};
}