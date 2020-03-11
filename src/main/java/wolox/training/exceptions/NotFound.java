package wolox.training.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NotFound extends ResponseStatusException  {
  public NotFound(String errorMessage) {
    super(HttpStatus.NOT_FOUND,errorMessage);
  }
}
