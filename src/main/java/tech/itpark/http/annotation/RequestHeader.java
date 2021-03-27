package tech.itpark.http.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// аннотации можно аннотировать аннотациями
// 1. Над чем можно писать
// 2. На каком уровне будет храниться:
// - SOURCE -> compiler, IDE, style checker
// - CLASS -> byte code
// - RUNTIME -> доступны из Reflection API
//@Retention(value = RetentionPolicy.RUNTIME)
// 1. value - имя специальное
@Retention(RetentionPolicy.RUNTIME) // value = RetentionPolicy.RUNTIME
// @Target({ElementType.PARAMETER})
// 2. Если в массиве всего один элемент, то {} можно не писать
@Target(ElementType.PARAMETER)
public @interface RequestHeader {
  String value();
  boolean required() default true; // если не задан этот элемент аннотации, то там true
  String defaultArgValue() default "";
}
