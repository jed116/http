package tech.itpark.http.exception;

public class MalformedRequestException extends RuntimeException {
  public MalformedRequestException() {
    super();
  }

  public MalformedRequestException(String message) {
    super(message);
  }

  public MalformedRequestException(String message, Throwable cause) {
    super(message, cause);
  }

  public MalformedRequestException(Throwable cause) {
    super(cause);
  }

  protected MalformedRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
