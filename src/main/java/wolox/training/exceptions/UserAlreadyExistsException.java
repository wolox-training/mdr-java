
package wolox.training.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import wolox.training.constants.StatusMessages;

public class UserAlreadyExistsException extends ResponseStatusException {
  public UserAlreadyExistsException() {
    super(HttpStatus.BAD_REQUEST, StatusMessages.USER_ALREADY_EXISTS);
  }
}
