package wolox.training.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import wolox.training.components.CustomAuthenticationProvider;
import wolox.training.components.IAuthenticationFacade;
import wolox.training.constants.StatusMessages;
import wolox.training.exceptions.NotFoundException;
import wolox.training.models.User;
import wolox.training.models.Book;
import wolox.training.repositories.BookRepository;
import wolox.training.repositories.UserRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.mockito.BDDMockito.given;

@WebMvcTest(UserController.class)
class UserControllerIntegrationTest {

  @Autowired
  private MockMvc mvc;

  @MockBean
  private UserRepository userRepository;
  @MockBean
  private BookRepository bookRepository;
  @MockBean
  private CustomAuthenticationProvider customAuthenticationProvider;
  @MockBean
  private IAuthenticationFacade iAuthenticationFacade;

  @Autowired
  private PasswordEncoder passwordEncoder;

  // write test cases here

  private User testUser, testUser2;
  private Book testBook;
  private String jsonUser, jsonBook;
  private String PASSWORD = "test";

  @BeforeEach
  public void createVariables() {
    testUser = new User("username","test name", LocalDate.of(1992, 02, 02), passwordEncoder.encode(PASSWORD));
    testUser2 = new User("username2","test name 2", LocalDate.of(1993, 02, 02), passwordEncoder.encode(PASSWORD));
    testBook = new Book("Doyle","image","title","subtitle","publisher","1234","500","isbn","terror");
    jsonUser = "{" +
        "\"name\": \"" + testUser.getName() + "\"," +
        "\"username\": \"" + testUser.getUsername() + "\"," +
        "\"birthdate\": \"" + testUser.getBirthdate() + "\"," +
        "\"password\": \"" + PASSWORD + "\"" +
        "}";
    jsonBook = "{\"id\": " + testBook.getId() + "}";
  }

  // GetLoggedUser

  @WithMockUser(username = "username")
  @Test
  public void givenUser_whenGetLoggedUser_thenReturnJsonUserObject() throws Exception {
    given(userRepository.findFirstByUsername("username")).willReturn(java.util.Optional.of(testUser));
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    given(iAuthenticationFacade.getAuthentication()).willReturn(auth);

    mvc.perform(MockMvcRequestBuilders.get("/api/users/me")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value(testUser.getName()))
        .andExpect(jsonPath("$.username").value(testUser.getUsername()));
  }

  @WithMockUser(username = "invalidUsername")
  @Test
  public void givenInvalidUser_whenGetLoggedUser_thenReturnJsonUserObject() throws Exception {
    given(userRepository.findFirstByUsername("invalidUsername")).willReturn(java.util.Optional.empty());
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    given(iAuthenticationFacade.getAuthentication()).willReturn(auth);

    mvc.perform(MockMvcRequestBuilders.get("/api/users/me")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  // GetById

  @WithMockUser(value = "test")
  @Test
  public void givenUser_whenGetUser_thenReturnJsonUserObject() throws Exception {
    given(userRepository.findById(testUser.getId())).willReturn(java.util.Optional.of(testUser));

    mvc.perform(MockMvcRequestBuilders.get("/api/users/" + testUser.getId())
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value(testUser.getName()));
  }

  @WithMockUser(value = "test")
  @Test
  public void givenNoUser_whenGetUser_thenReturnNotFoundError() throws Exception {
    given(userRepository.findById(testUser.getId())).willThrow(new NotFoundException(StatusMessages.USER_NOT_FOUND));

    mvc.perform(MockMvcRequestBuilders.get("/api/users/" + testUser.getId())
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  // GetAll

  @WithMockUser(value = "test")
  @Test
  public void givenUserList_whenGetUsers_thenReturnJsonUserArray() throws Exception {
    Iterable<User> userIterable = Arrays.asList(testUser, testUser2);

    given(userRepository.findAll()).willReturn(userIterable);

    mvc.perform(MockMvcRequestBuilders.get("/api/users/")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].name").value(testUser.getName()))
        .andExpect(jsonPath("$[1].name").value(testUser2.getName()));
  }

  @WithMockUser(value = "test")
  @Test
  public void givenEmptyUserList_whenGetUsers_thenReturnJsonEmptyArray() throws Exception {
    Iterable<User> userIterable = new ArrayList<User>();

    given(userRepository.findAll()).willReturn(userIterable);

    mvc.perform(MockMvcRequestBuilders.get("/api/users/")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)));
  }

  // Search By

  @WithMockUser(value = "test")
  @Test
  public void givenUserList_whenSearchUsers_thenReturnJsonUserArray() throws Exception {
    Iterable<User> userIterable = Arrays.asList(testUser);

    given(userRepository.findAllWithFilters(
        testUser.getBirthdate(),
        testUser.getName(),
        testUser.getUsername()))
        .willReturn(userIterable);

    mvc.perform(MockMvcRequestBuilders.get("/api/users/search-by")
        .contentType(MediaType.APPLICATION_JSON)
        .queryParam("birthdate", testUser.getBirthdate().toString())
        .queryParam("name", testUser.getName())
        .queryParam("username", testUser.getUsername()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].name").value(testUser.getName()));
  }

  @WithMockUser(value = "test")
  @Test
  public void givenUserList_whenSearchUsersWithNullParams_thenReturnJsonUserArray() throws Exception {
    Iterable<User> userIterable = Arrays.asList(testUser);

    given(userRepository.findAllWithFilters(
        null,
        testUser.getName(),
        ""))
        .willReturn(userIterable);

    mvc.perform(MockMvcRequestBuilders.get("/api/users/search-by")
        .contentType(MediaType.APPLICATION_JSON)
        .queryParam("name", testUser.getName()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].name").value(testUser.getName()));
  }

  @WithMockUser(value = "test")
  @Test
  public void givenEmptyUserList_whenSearchUsers_thenReturnJsonEmptyArray() throws Exception {
    Iterable<User> userIterable = new ArrayList<User>();

    given(userRepository.findAllWithFilters(
        null,
        "",
        ""))
        .willReturn(userIterable);

    mvc.perform(MockMvcRequestBuilders.get("/api/users/search-by")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)));
  }

  // Create User

  @WithMockUser(value = "test")
  @Test
  public void givenUser_whenPostUsers_thenReturnJsonUserObject() throws Exception {
    given(userRepository.save(any(User.class))).willReturn(testUser);
    given(userRepository.findFirstByUsername(testUser.getUsername())).willReturn(Optional.empty());

    mvc.perform(MockMvcRequestBuilders.post("/api/users/")
        .contentType(MediaType.APPLICATION_JSON)
        .content(jsonUser))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value(testUser.getName()));
  }

  // Edit User

  @WithMockUser(value = "test")
  @Test
  public void givenUser_whenPutUsers_thenReturnJsonUserObject() throws Exception {
    given(userRepository.save(any(User.class))).willReturn(testUser);
    given(userRepository.findById(testUser.getId())).willReturn(java.util.Optional.ofNullable(testUser));

    mvc.perform(MockMvcRequestBuilders.put("/api/users/"+testUser.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .content(jsonUser))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value(testUser.getName()));
  }

  @WithMockUser(value = "test")
  @Test
  public void givenUnknownUser_whenPutUsers_thenReturnNotFoundError() throws Exception {
    mvc.perform(MockMvcRequestBuilders.put("/api/users/"+testUser.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .content(jsonUser))
        .andExpect(status().isNotFound());
  }

  // Change user password

  @WithMockUser(value = "test")
  @Test
  public void givenNewAndOldPassword_whenPutUsersPassword_thenChangeUserPassword() throws Exception {
    given(userRepository.findById(testUser.getId())).willReturn(java.util.Optional.ofNullable(testUser));
    given(userRepository.save(any(User.class))).willReturn(testUser);

    mvc.perform(MockMvcRequestBuilders.put("/api/users/" + testUser.getId() + "/password")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"password\": \"newPassword\", \"old_password\": \"" + PASSWORD + "\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value(testUser.getName()));
  }

  @WithMockUser(value = "test")
  @Test
  public void givenNewPassword_whenPutUsersPassword_thenFailsForMissingField() throws Exception {
    given(userRepository.findById(testUser.getId())).willReturn(java.util.Optional.ofNullable(testUser));

    mvc.perform(MockMvcRequestBuilders.put("/api/users/" + testUser.getId() + "/password")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"password\": \"newPassword\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(status().reason(StatusMessages.CHANGE_PASSWORD_FIELDS));
  }

  @WithMockUser(value = "test")
  @Test
  public void givenNewAndOldPassword_whenPutUsersPassword_thenFailsForWrongPassword() throws Exception {
    given(userRepository.findById(testUser.getId())).willReturn(java.util.Optional.ofNullable(testUser));

    mvc.perform(MockMvcRequestBuilders.put("/api/users/" + testUser.getId() + "/password")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"password\": \"newPassword\", \"old_password\": \"badOldPassword\"}"))
        .andExpect(status().isForbidden())
        .andExpect(status().reason(StatusMessages.INVALID_PASSWORD));
  }

  @WithMockUser(value = "test")
  @Test
  public void givenNewAndOldPassword_whenPutUsersPassword_thenFailsForSamePassword() throws Exception {
    given(userRepository.findById(testUser.getId())).willReturn(java.util.Optional.ofNullable(testUser));

    mvc.perform(MockMvcRequestBuilders.put("/api/users/" + testUser.getId() + "/password")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"password\": \"" + PASSWORD + "\", \"old_password\": \"" + PASSWORD + "\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(status().reason(StatusMessages.OLD_AND_NEW_PASSWORDS));
  }

  // Delete User

  @WithMockUser(value = "test")
  @Test
  public void whenDeleteUsers_thenReturnOkNoContent() throws Exception {
    given(userRepository.findById(testUser.getId())).willReturn(java.util.Optional.ofNullable(testUser));

    mvc.perform(MockMvcRequestBuilders.delete("/api/users/"+testUser.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .content(jsonUser))
        .andExpect(status().isNoContent());
  }

  @WithMockUser(value = "test")
  @Test
  public void givenUnknownUser_whenDeleteUsers_thenReturnNotFoundError() throws Exception {
    mvc.perform(MockMvcRequestBuilders.delete("/api/users/"+testUser.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .content(jsonUser))
        .andExpect(status().isNotFound());
  }

  // Add book

  @WithMockUser(value = "test")
  @Test
  public void givenUserAndBook_whenAddBookToUsers_thenReturnJSONObject() throws Exception {
    given(userRepository.findById(testUser.getId())).willReturn(java.util.Optional.ofNullable(testUser));
    given(bookRepository.findById(testBook.getId())).willReturn(java.util.Optional.ofNullable(testBook));
    given(userRepository.save(any(User.class))).willReturn(testUser);

    mvc.perform(MockMvcRequestBuilders.put("/api/users/"+testUser.getId()+"/books/add")
        .contentType(MediaType.APPLICATION_JSON)
        .content(jsonBook))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value(testUser.getName()))
        .andExpect(jsonPath("$.books[0].title").value(testBook.getTitle()));
  }

  @WithMockUser(value = "test")
  @Test
  public void givenJustBook_whenAddBookToUsers_thenReturnNotFoundError() throws Exception {
    given(userRepository.findById(testUser.getId())).willReturn(java.util.Optional.ofNullable(testUser));
    given(userRepository.save(any(User.class))).willReturn(testUser);

    mvc.perform(MockMvcRequestBuilders.put("/api/users/"+testUser.getId()+"/books/add")
        .contentType(MediaType.APPLICATION_JSON)
        .content(jsonBook))
        .andExpect(status().isNotFound());
  }

  @WithMockUser(value = "test")
  @Test
  public void givenJustUser_whenAddBookToUsers_thenReturnNotFoundError() throws Exception {
    given(bookRepository.findById(testBook.getId())).willReturn(java.util.Optional.ofNullable(testBook));

    mvc.perform(MockMvcRequestBuilders.put("/api/users/"+testUser.getId()+"/books/add")
        .contentType(MediaType.APPLICATION_JSON)
        .content(jsonBook))
        .andExpect(status().isNotFound());
  }

  @WithMockUser(value = "test")
  @Test
  public void givenUserWithBookAndBook_whenAddBookToUsers_thenReturnJSONObject() throws Exception {
    given(userRepository.findById(testUser.getId())).willReturn(java.util.Optional.ofNullable(testUser));
    given(bookRepository.findById(testBook.getId())).willReturn(java.util.Optional.ofNullable(testBook));
    given(userRepository.save(any(User.class))).willReturn(testUser);

    testUser.addBook(testBook);

    mvc.perform(MockMvcRequestBuilders.put("/api/users/"+testUser.getId()+"/books/add")
        .contentType(MediaType.APPLICATION_JSON)
        .content(jsonBook))
        .andExpect(status().isConflict());
  }

  // Remove book

  @WithMockUser(value = "test")
  @Test
  public void givenUserAndBook_whenDeleteBookToUsers_thenReturnJSONObject() throws Exception {
    given(userRepository.findById(testUser.getId())).willReturn(java.util.Optional.ofNullable(testUser));
    given(bookRepository.findById(testBook.getId())).willReturn(java.util.Optional.ofNullable(testBook));
    given(userRepository.save(any(User.class))).willReturn(testUser);

    testUser.addBook(testBook);

    mvc.perform(MockMvcRequestBuilders.put("/api/users/"+testUser.getId()+"/books/remove")
        .contentType(MediaType.APPLICATION_JSON)
        .content(jsonBook))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value(testUser.getName()))
        .andExpect(jsonPath("$.books", hasSize(0)));
  }

  @WithMockUser(value = "test")
  @Test
  public void givenJustBook_whenDeleteBookToUsers_thenReturnNotFoundError() throws Exception {
    given(bookRepository.findById(testBook.getId())).willReturn(java.util.Optional.ofNullable(testBook));

    testUser.addBook(testBook);

    mvc.perform(MockMvcRequestBuilders.put("/api/users/"+testUser.getId()+"/books/remove")
        .contentType(MediaType.APPLICATION_JSON)
        .content(jsonBook))
        .andExpect(status().isNotFound());
  }

  @WithMockUser(value = "test")
  @Test
  public void givenJustUser_whenDeleteBookToUsers_thenReturnNotFoundError() throws Exception {
    given(userRepository.findById(testUser.getId())).willReturn(java.util.Optional.ofNullable(testUser));
    given(userRepository.save(any(User.class))).willReturn(testUser);

    testUser.addBook(testBook);

    mvc.perform(MockMvcRequestBuilders.put("/api/users/"+testUser.getId()+"/books/remove")
        .contentType(MediaType.APPLICATION_JSON)
        .content(jsonBook))
        .andExpect(status().isNotFound());
  }

  @WithMockUser(value = "test")
  @Test
  public void givenUserWithoutBookAndBook_whenDeleteBookToUsers_thenReturnJSONObject() throws Exception {
    given(userRepository.findById(testUser.getId())).willReturn(java.util.Optional.ofNullable(testUser));
    given(bookRepository.findById(testBook.getId())).willReturn(java.util.Optional.ofNullable(testBook));
    given(userRepository.save(any(User.class))).willReturn(testUser);

    mvc.perform(MockMvcRequestBuilders.put("/api/users/"+testUser.getId()+"/books/remove")
        .contentType(MediaType.APPLICATION_JSON)
        .content(jsonBook))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value(testUser.getName()))
        .andExpect(jsonPath("$.books", hasSize(0)));
  }
}
