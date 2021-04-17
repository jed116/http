package tech.itpark.http.server;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

// immutable
@RequiredArgsConstructor
@Data
public class Response {
  private final int statusCode;
  private final String statusText;
  private final String version;
  private final Map<String, List<String>> headers;
  private final byte[] body;
}
