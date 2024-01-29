/* (C) 2024 AladdinSystem License */
package aladdinsys.api.task.Exception;


import com.google.gson.JsonObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(CustomServiceException.class)
  public ResponseEntity<String> handleCustomServiceException(CustomServiceException ex) {
    JsonObject jsonResponse = new JsonObject();
    jsonResponse.addProperty("success", false);
    jsonResponse.addProperty("message", ex.getMessage());

    return ResponseEntity.status(ex.getStatus()).body(jsonResponse.toString());
  }
}
