package com.apkc.archtype.quals;

import java.lang.annotation.*;

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface Pattern {
    public String name();
    public String kind();
    public String role();
}
