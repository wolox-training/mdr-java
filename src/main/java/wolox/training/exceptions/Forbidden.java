package wolox.training.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class Forbidden extends ResponseStatusException {
  public Forbidden(String errorMessage) {
    super(HttpStatus.FORBIDDEN,errorMessage);
  }
}
