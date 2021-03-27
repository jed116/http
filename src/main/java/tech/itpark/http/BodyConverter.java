package tech.itpark.http;

public interface BodyConverter {
//  boolean canRead(Request request, Class<?> cls);
  boolean canRead(Request request);
  boolean canWrite(Class<?> cls);
  <T> T convert(Request request, Class<T> cls);
}
