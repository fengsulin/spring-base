package com.lin.spring.anno;

import com.lin.spring.constant.ScopePolicy;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface Scope {
    String value() default ScopePolicy.SINGLE;
}
