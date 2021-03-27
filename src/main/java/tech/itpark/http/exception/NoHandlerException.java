package tech.itpark.http.exception;

public class NoHandlerException extends RuntimeException {
  public NoHandlerException() {
    super();
  }

  public NoHandlerException(String message) {
    super(message);
  }

  public NoHandlerException(String message, Throwable cause) {
    super(message, cause);
  }

  public NoHandlerException(Throwable cause) {
    super(cause);
  }

  protected NoHandlerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
