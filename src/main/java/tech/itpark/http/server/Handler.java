package tech.itpark.http.server;

import java.io.OutputStream;

@FunctionalInterface
public interface Handler {
  void handle(Request request, OutputStream responseStream);
}
