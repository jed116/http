package tech.itpark.http;

import lombok.extern.jackson.Jacksonized;
import tech.itpark.http.annotation.RequestBody;
import tech.itpark.http.annotation.RequestHeader;
import tech.itpark.http.exception.NoHandlerException;
import tech.itpark.http.exception.UnresolvedHandlerParametersException;
import tech.itpark.http.exception.MalformedRequestException;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.stream.Collectors;

// TCP/IP
public class Server {
  private final Map<String, Map<String, Object>> routers = new HashMap<>();
  // resolve
  private final List<HandleMethodResolver>  handleMethodResolvers = new LinkedList<>();
  private final List<BodyConverter>         bodyConverters        = new LinkedList<>();

  // TODO: придумать имя
  public void register(HandleMethodResolver resolver) {
    handleMethodResolvers.add(resolver);
  }

  public void registerConverter(BodyConverter bodyConverter) {
    bodyConverters.add(bodyConverter);
  }



  public void registerHandlerForMethodAndPath(String method, String path, Object handler) {
    // final var map = Optional.ofNullable(routers.get(method))
    //     .orElse(new HashMap<>());
    // map.put(path, handler);
    // routers.put(method, map);
    // Runnable
    Optional.ofNullable(routers.get(method.toLowerCase())).ifPresentOrElse(
            map -> map.put(path.toUpperCase(), handler),
            () -> routers.put(method.toLowerCase(), new HashMap<>(Map.of(path.toLowerCase(), handler)))
                                                                          );
  }




  public void start(int port) {
    // ServerSocket - listening
    // Socket <-> Socket
    try (
        final var serverSocket = new ServerSocket(port);
    ) {
      while (true) {
//        handleConnection(serverSocket.accept()); // accept() -> blocking, пока не получит Socket
        Socket accept = serverSocket.accept();
        handleConnection(accept); // accept() -> blocking, пока не получит Socket
      }
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    } finally {
      System.out.println("STOPed!");
    }
  }

  private void handleConnection(Socket socket) {
    try (
        socket;
        final var out = new BufferedOutputStream(socket.getOutputStream());
        final var in = new BufferedInputStream(socket.getInputStream());
    ) {
      final var limit = 4096;

      in.mark(limit); // 4096

      // proof of concept

      final var buffer = new byte[limit];
      final var read = in.read(buffer);

      final var CRLF = new byte[]{'\r', '\n'}; // char

      // GET / HTTP1.1\r\n
      final var filled = new byte[read];
      System.arraycopy(buffer, 0, filled, 0, read);

      final var requestLineEndPosition = indexOf(filled, CRLF, 0) + CRLF.length;
//      int contentLength = 0;
      // GET / HTTP/1.1\r\n

      // TODO: filled[requestLineEndPosition] = X -> [XHost: ya.ru\r\n]
      //System.arraycopy(filled, 0, filled, 0, requestLineEndPosition);

// Request
      String requestFistLine = new String(Arrays.copyOf(filled, requestLineEndPosition));
      String [] requestFistLineArray = requestFistLine.split(" ");
      if (requestFistLineArray.length != 3){
          throw new MalformedRequestException("Content-Length not found");
      }

      String requestMethod  = requestFistLineArray[0].trim().toLowerCase();
      String requestPath    = requestFistLineArray[1].trim().toLowerCase();
      String requestVersion = requestFistLineArray[2].trim().toLowerCase();

//Headers
      Map<String, List<String>> requestHeaders = new HashMap<>();

      int previousEndPosition = requestLineEndPosition;
      // Headers
      while (true) {
        var currentEndPosition = indexOf(filled, CRLF, previousEndPosition);
        if (currentEndPosition == -1) {
//          throw new MalformedRequestException("Content-Length not found");
          break;
        } else {
          currentEndPosition = currentEndPosition + CRLF.length;
        }

        final var header = new byte[currentEndPosition - previousEndPosition];
        System.arraycopy(
                filled,
                previousEndPosition,
                header,
                0,
                currentEndPosition - previousEndPosition);

        previousEndPosition = currentEndPosition;

        // TODO: Content-Length: 11\r\n

        final var headerString = new String(header);
        String[] headerStringParts = headerString.split(":", 2);
        if (headerString.length() > 2){

          if (headerStringParts.length != 2) {
            throw new MalformedRequestException("Header doesn't contain :");
          }
          String headerTitle = headerStringParts[0].trim().toLowerCase();
          List<String> headerValues = Arrays.asList(headerStringParts[1].split(",")).stream().
                map(String::trim).map(String::toLowerCase).collect(Collectors.toList());

          requestHeaders.put(headerTitle, headerValues);
        }
      }

//Body
      byte[] requestBody = new byte[0];

      if (requestHeaders.containsKey("content-length")){
        // 4096 - max Request Line + Headers
        final var CRLFCRLF = new byte[]{'\r', '\n', '\r', '\n'};
        final var headersEndPosition = indexOf(filled, CRLFCRLF, 0) + CRLFCRLF.length;
        int contentLength;
        try{
          contentLength = Integer.parseInt(requestHeaders.get("content-length").get(0));
        }catch (NumberFormatException e){
          contentLength = 0;
        }
        if (contentLength > 0){
          in.reset(); // возвращаемся на точку, в которой установили mark
          final var fullRequest = in.readNBytes(headersEndPosition + contentLength);
          requestBody = new byte[contentLength];
          System.arraycopy(fullRequest, headersEndPosition, requestBody, 0, contentLength);
        }

      }

// ВЫШЕ СОБРАЛ РЕКВЕСТ - распасрил все из входящего потока
      Request request = new Request( requestMethod, requestPath, requestVersion, requestHeaders, requestBody);

      // TODO: 1. Собрать Request (нужно где-то наверху вспомнить, что у GET Content-Length нет)
      // TODO: 2. Вызвать нужный handler

      // TODO: server.register("GET", "/api/users", handler);
      // TODO: https://pastebin.com/51Dk6QmJ

// БЬЛО У ВАС
//      final var handler = routers.get("GET").get("/api/users");
// А НУЖНО ТАК - я правильно понимаю??? Для этого мы объект request собирали???
      final var handler = routers.get(request.getMethod()).get(request.getPath());

// БЬЛО У ВАС
//      callHandler(handler, null, out); // вызов handler'а
// А НУЖНО ТАК - я правильно понимаю??? Для этого мы объект request собирали???
      if (handler != null){
        callHandler(handler, request, out); // вызов handler'а
      }


    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  private void callHandler( Object handler, Request request, OutputStream responseStream)
  {
    try {
      for (final var resolver : handleMethodResolvers) {
        final var method = resolver.resolve(handler);
        if (method.isPresent()) {
          callAdapter(method.get(), handler, request, responseStream);
          return;
        }
      }
    } catch (IllegalAccessException | InvocationTargetException e) {
      e.printStackTrace();
      // TODO: handle exception -> 500 Server Error
      return;
    }
    throw new NoHandlerException();
  }


  private void callAdapter(
      Method method,
      Object handler,
      Request request,
      OutputStream responseStream ) throws InvocationTargetException, IllegalAccessException {
    // 1: Handler -> handle(Request, OutputStream)
    // 2: Object -> method(), method(Request), method(OutputStream), ..., method(???),
    final var params = method.getParameters();
    final var args = new LinkedList<>();
    // FIXME: IMPROVEMENT FOR DI
    // Parameter/Argument Resolving
    for (Parameter param : params) {
      final var parameterType = param.getType();

      // Annotation first
      // смотрим, есть ли над текущим параметром наша аннотация
      if (param.isAnnotationPresent(RequestHeader.class)) {

        if (!parameterType.equals(String.class)) {
          // TODO: Exception - header's argument type wrong
          break;
        }
        final var annotation = param.getAnnotation(RequestHeader.class);
        final var value = annotation.value().toLowerCase();

        if (value.isEmpty()){
          // TODO: Exception -  header's annotation value not set (empty)
          break;
        }

        if(request.getHeaders().containsKey(value)){
          args.add(String.join(" , ", request.getHeaders().get(value)) ); // List<String> >>> String
          continue;
        }

        if (annotation.required()) {
          //TODO: Exception - request header not found
          break;
        }

        args.add(annotation.defaultArgValue());
        continue;

        // Gson -> Definition

      }

      if (param.isAnnotationPresent(RequestBody.class)) {
        // BodyConverter ->
        for (BodyConverter converter : bodyConverters) {
          if (converter.canRead(request)) {
            args.add(converter.convert(request, param.getType()));
            break;
          }
        }
        // TODO: move to method
        // TODO: if we didn't find body converter - OOPS!
      }


      if (parameterType.equals(Request.class)) {
        args.add(request);
        continue;
      }

      if (parameterType.equals(OutputStream.class)) {
        args.add(responseStream);
        continue;
      }
    }

    if (params.length != args.size()) {
      // надо написать: у какого метода, какие параметры не нашли
      throw new UnresolvedHandlerParametersException();
    }

    method.setAccessible(true);
    method.invoke(handler, args.toArray());
  }

  // from Guava: Bytes.java
  public static int indexOf(byte[] array, byte[] target, int start) {
    // checkNotNull(array, "array");
    // checkNotNull(target, "target");
    if (target.length == 0) {
      return 0;
    }

    outer:
    for (int i = start; i < array.length - target.length + 1; i++) {
      for (int j = 0; j < target.length; j++) {
        if (array[i + j] != target[j]) {
          continue outer;
        }
      }
      return i;
    }
    return -1;
  }
}
