/* (C) 2024 AladdinSystem License */
package aladdinsys.api.task.Exception;


import org.springframework.http.HttpStatus;

public class CustomServiceException extends RuntimeException {
  private HttpStatus status;
  private String message;

  public CustomServiceException(HttpStatus status, String message) {
    super(message);
    this.status = status;
    this.message = message;
  }

  public HttpStatus getStatus() {
    return status;
  }

  @Override
  public String getMessage() {
    return message;
  }
}
