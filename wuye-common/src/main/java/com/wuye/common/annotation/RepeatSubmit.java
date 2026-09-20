package com.wuye.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Prevent duplicate form submission.
 *
 * @author wuye
 */
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RepeatSubmit
{
    /**
     * Interval in milliseconds. Submissions inside this window are treated as duplicates.
     */
    int interval() default 5000;

    /**
     * Prompt message returned to the client.
     */
    String message() default "Duplicate submission is not allowed, please try again later";
}
