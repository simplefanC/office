package com.simplefanc.office.common.auth;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresPermissions {
    String[] value();

    Logical logical() default Logical.AND;
}
