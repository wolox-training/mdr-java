package wolox.training.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import wolox.training.constants.ErrorMessages;

public class BookAlreadyOwnedException extends ResponseStatusException {
  public BookAlreadyOwnedException() {
    super(HttpStatus.CONFLICT, ErrorMessages.BOOK_ALREADY_ADDED);
  }
}
