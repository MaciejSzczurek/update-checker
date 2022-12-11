package com.maciejszczurek.updatechecker.checker.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ApplicationType {
  com.maciejszczurek.updatechecker.application.model.ApplicationType value();
}
