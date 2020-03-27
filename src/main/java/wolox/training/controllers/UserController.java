package wolox.training.controllers;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import wolox.training.components.IAuthenticationFacade;
import wolox.training.constants.StatusMessages;
import wolox.training.dtos.UserDTO;
import wolox.training.exceptions.BadRequestException;
import wolox.training.exceptions.ForbiddenException;
import wolox.training.exceptions.NotFoundException;
import wolox.training.exceptions.UserAlreadyExistsException;
import wolox.training.models.Book;
import wolox.training.models.User;
import wolox.training.repositories.BookRepository;
import wolox.training.repositories.UserRepository;

import java.time.LocalDate;
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
  @Autowired
  private IAuthenticationFacade authenticationFacade;

  @GetMapping("/me")
  public User currentUserNameSimple() {
    Authentication authentication = authenticationFacade.getAuthentication();
    return userRepository.findFirstByUsername(authentication.getName()).orElseThrow(() -> new NotFoundException(StatusMessages.USER_NOT_FOUND));
  }

  @GetMapping("/{id}")
  public User read(@PathVariable Long id) {
    return userRepository.findById(id).orElseThrow(() -> new NotFoundException(StatusMessages.USER_NOT_FOUND));
  }

  @GetMapping
  public Page<User> readAll(Pageable pageable) {
    return userRepository.findAll(pageable);
  }

  @GetMapping("/search")
  public Page<User> findAllByBirthdateAndName(
      @RequestParam(required=false, defaultValue = "") String startDate,
      @RequestParam(required=false, defaultValue = "") String endDate,
      @RequestParam(required=false, defaultValue = "") String name,
      Pageable pageable) {
    LocalDate startLocalDate = !startDate.isEmpty() ? LocalDate.parse(startDate) : null;
    LocalDate endLocalDate = !endDate.isEmpty() ? LocalDate.parse(endDate) : null;
    return userRepository.findAllByBirthdateAndName(startLocalDate,endLocalDate,name,pageable);
  }

  @GetMapping("/search-by")
  public Page<User> readAllWithFilters(
      @RequestParam(required=false, defaultValue = "") String birthdate,
      @RequestParam(required=false, defaultValue = "") String name,
      @RequestParam(required=false, defaultValue = "") String username,
      Pageable pageable) {
    LocalDate parsedBirthdate = !birthdate.isEmpty() ? LocalDate.parse(birthdate) : null;
    return userRepository.findAllWithFilters(parsedBirthdate,name,username,pageable);
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
  public User update(@RequestBody UserDTO receivedUser, @PathVariable Long id) {
    User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException(StatusMessages.USER_NOT_FOUND));
    if (receivedUser.getName() != null) user.setName(receivedUser.getName());
    if (receivedUser.getUsername() != null) user.setUsername(receivedUser.getUsername());
    if (receivedUser.getBirthdate() != null) user.setBirthdate(receivedUser.getBirthdate());
    return userRepository.save(user);
  }

  @PutMapping("/{id}/password")
  public User updatePassword(@RequestBody UserDTO receivedUser, @PathVariable Long id) {
    User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException(StatusMessages.USER_NOT_FOUND));
    if (receivedUser.getPassword() == null || receivedUser.getOldPassword() == null)
      throw new BadRequestException(StatusMessages.CHANGE_PASSWORD_FIELDS);
    if (!passwordEncoder.matches(receivedUser.getOldPassword(),user.getPassword()))
      throw new ForbiddenException(StatusMessages.INVALID_PASSWORD);
    if (receivedUser.getPassword().equals(receivedUser.getOldPassword()))
      throw new BadRequestException(StatusMessages.OLD_AND_NEW_PASSWORDS);
    user.setPassword(passwordEncoder.encode(receivedUser.getPassword()));

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