package tech.itpark;

import tech.itpark.http.annotation.RequestBody;
import tech.itpark.http.annotation.RequestHeader;
import tech.itpark.http.HandleMethodResolver;
import tech.itpark.http.Server;
import tech.itpark.http.converter.JsonBodyConverter;
import tech.itpark.http.converter.XmlBodyConverter;
import tech.itpark.http.model.UserModel;

import java.io.OutputStream;
import java.util.Optional;

public class Main {
  public static void main(String[] args) {
    final var server = new Server();
    // TODO: server.register("GET", "/api/users", handler);
    // TODO: method reference
    // 1. Class -> Static method
    // 2. Object.method -> Class::method
    // 3. ... ?
    // server.register((Object handler) -> HandleMethodResolver.handlerMethodResolver(handler));

    server.register(HandleMethodResolver::handlerMethodResolver);
    server.register(HandleMethodResolver::singlePublicMethodResolver);
//        server.GET("/api/users", (request, responseStream) -> {
//            System.out.println("hello world");
//        });

    // local class: для тестирования второго типа Resolver'ов: классы с одним методом
//        abstract class CustomHandler {
//            public abstract void handle(OutputStream stream);
//        }
//        server.GET("/api/users", new CustomHandler() {
//            @Override
//            public void handle(OutputStream stream) {
//                System.out.println("finish");
//            }
//        });

    server.registerConverter(new JsonBodyConverter());
    server.registerConverter(new XmlBodyConverter());

    abstract class AdvancedHandler {
//      public abstract void handle();
    }

    server.registerHandlerForMethodAndPath("POST", "/api/users", new AdvancedHandler() {
//      @Override
      public void handle(@RequestHeader("Content-Type") String contentType,
                         @RequestBody UserModel userModel,
                         OutputStream responseStream)
      {
        System.out.println("Gotcha!");
      }
    });

//    server.GET("/api/users/me", new AdvancedHandler() {
//      @Override
//      public void handle(@RequestHeader("Authorization") String authorization) {
//        System.out.println("finish");
//      }
//    });
    server.start(8888);//server.start(9999);
  }
}
