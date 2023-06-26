package com.xiaopeng.server.vx.config;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AutoLog {

    /**
     * 日志内容
     *
     * @return
     */
    String value() default "";

}