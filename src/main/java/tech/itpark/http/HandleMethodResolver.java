package tech.itpark.http;

import java.io.OutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.Optional;

@FunctionalInterface
public interface HandleMethodResolver {
  Optional<Method> resolve(Object handler);

  static Optional<Method> handlerMethodResolver(Object handler) {
    try {
      if (handler instanceof Handler) {
        return Optional.of(handler.getClass().getMethod("handle", Request.class, OutputStream.class));
      }
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    }
    return Optional.empty();
  }

  static Optional<Method> singlePublicMethodResolver(Object handler) {
    final var methods = handler.getClass().getDeclaredMethods();
    final var publicMethods = new LinkedList<Method>();
    for (Method method : methods) {
      final var modifiers = method.getModifiers();
      if (Modifier.isPublic(modifiers)) {
        publicMethods.add(method);
      }
    }
    if (publicMethods.size() != 1) {
      return Optional.empty();
    }
    return Optional.of(publicMethods.get(0));
  }
}
