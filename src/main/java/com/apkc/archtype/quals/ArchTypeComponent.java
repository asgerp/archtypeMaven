package com.apkc.archtype.quals;

import java.lang.annotation.*;

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface ArchTypeComponent {
    Pattern[] patterns() default{};

}
