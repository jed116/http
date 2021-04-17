package tech.itpark.http.converter;

public interface BodyConverter {
  boolean isConverted(String contentType);
  <T> T convert(byte[] body, Class<T> cls);
  <T> String unconvert(T object);
}
