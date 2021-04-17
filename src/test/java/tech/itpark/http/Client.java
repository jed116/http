package tech.itpark.http;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Client {
  public static void main(String[] args) {
    // pattern builder -> промежуточный объект нужный только для удобного создания объекта другого класса
    final var client = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_1_1)
        .build();

//POST
    final var request = HttpRequest.newBuilder()
        .uri(URI.create("http://localhost:8888/API/uSeRs/"))
        .setHeader ("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString("{\"id\": 1, \"name\": \"First\" }"))
        .build();



////GET
//    final var request = HttpRequest.newBuilder()
//        .uri(URI.create("http://localhost:9999/api/users"))
//        .GET()
//        .build();

    try {
//      final var response = client.send(request, HttpResponse.BodyHandlers.discarding());
      final var response = client.send(request, HttpResponse.BodyHandlers.ofString());
      return;
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
