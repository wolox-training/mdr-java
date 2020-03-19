package wolox.training.controllers;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import wolox.training.constants.StatusMessages;
import wolox.training.dtos.UserDto;
import wolox.training.exceptions.ForbiddenException;
import wolox.training.exceptions.NotFoundException;
import wolox.training.exceptions.UserAlreadyExistsException;
import wolox.training.models.Book;
import wolox.training.models.User;
import wolox.training.repositories.BookRepository;
import wolox.training.repositories.UserRepository;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

  @Autowired
  private UserRepository userRepository;
  @Autowired
  private BookRepository bookRepository;
  @Autowired
  private PasswordEncoder passwordEncoder;

  @GetMapping("/{id}")
  public User read(@PathVariable Long id) {
    return userRepository.findById(id).orElseThrow(() -> new NotFoundException(StatusMessages.USER_NOT_FOUND));
  }

  @GetMapping
  public Iterable<User> readAll() {
    return userRepository.findAll();
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public User create(@RequestBody User user) {
    String rawPassword = user.getPassword();
    user.setPassword(passwordEncoder.encode(rawPassword));
    Optional<User> foundUser = userRepository.findFirstByUsername(user.getUsername());
    if (foundUser.isPresent()) throw new UserAlreadyExistsException();
    return userRepository.save(user);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(code = HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    userRepository.findById(id).orElseThrow(() -> new NotFoundException(StatusMessages.USER_NOT_FOUND));
    userRepository.deleteById(id);
  }

  @PutMapping("/{id}")
  public User update(@RequestBody UserDto receivedUser, @PathVariable Long id) {
    User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException(StatusMessages.USER_NOT_FOUND));
    if (receivedUser.getName() != null) user.setName(receivedUser.getName());
    if (receivedUser.getUsername() != null) user.setUsername(receivedUser.getUsername());
    if (receivedUser.getBirthdate() != null) user.setBirthdate(receivedUser.getBirthdate());
    return userRepository.save(user);
  }

  @PutMapping("/{id}/password")
  public User updatePassword(@RequestBody UserDto receivedUser, @PathVariable Long id) {
    User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException(StatusMessages.USER_NOT_FOUND));
    if (receivedUser.getPassword() != null) user.setPassword(passwordEncoder.encode(receivedUser.getPassword()));
    return userRepository.save(user);
  }

  @PutMapping("/{userId}/books/add")
  @ApiOperation(value= "Add a book to an user. The book must exist.")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = StatusMessages.BOOK_ADDED),
      @ApiResponse(code = 404, message = StatusMessages.BOOK_NOT_FOUND),
      @ApiResponse(code = 404, message = StatusMessages.USER_NOT_FOUND)
  })
  public User addBook(@ApiParam(value = "Book object to be added to the user") @RequestBody Book receivedBook, @PathVariable Long userId) {
    User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(StatusMessages.USER_NOT_FOUND));
    Book book = bookRepository.findById(receivedBook.getId()).orElseThrow(() -> new NotFoundException(StatusMessages.BOOK_NOT_FOUND));
    user.addBook(book);
    return userRepository.save(user);
  }

  @PutMapping("/{userId}/books/remove")
  public User removeBook(@RequestBody Book receivedBook, @PathVariable Long userId) {
    User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(StatusMessages.USER_NOT_FOUND));
    Book book = bookRepository.findById(receivedBook.getId()).orElseThrow(() -> new NotFoundException(StatusMessages.BOOK_NOT_FOUND));
    user.removeBook(book);
    return userRepository.save(user);
  }
}