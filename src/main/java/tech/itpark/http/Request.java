package tech.itpark.http;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

// immutable
@RequiredArgsConstructor
@Data
public class Request {
  private final String method; // GET
  private final String path; // /
  private final String version; // version
  private final Map<String, List<String>> headers;
  private final byte[] body;
}
