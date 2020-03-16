package wolox.training.controllers;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import wolox.training.constants.StatusMessages;
import wolox.training.exceptions.NotFoundException;
import wolox.training.models.User;
import wolox.training.models.Book;
import wolox.training.repositories.BookRepository;
import wolox.training.repositories.UserRepository;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
class UserControllerIntegrationTest {

  @Autowired
  private MockMvc mvc;

  @MockBean
  private UserRepository userRepository;
  @MockBean
  private BookRepository bookRepository;

  // write test cases here

  private User testUser = new User("username","test name", LocalDate.of(1992, 02, 02));
  private User testUser2 = new User("username2","test name 2", LocalDate.of(1993, 02, 02));
  private Book testBook = new Book("Doyle","image","title","subtitle","publisher","1234","500","isbn","terror");
  private JSONObject jsonUser, jsonBook;

  {
    try {
      jsonUser = new JSONObject()
            .put("name", testUser.getName())
            .put("username", testUser.getUsername())
            .put("birthdate", testUser.getBirthdate());
      jsonBook = new JSONObject().put("id", testBook.getId());
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  // GetById

  @Test
  public void givenUser_whenGetUser_thenReturnJsonUserObject() throws Exception {
    given(userRepository.findById(testUser.getId())).willReturn(java.util.Optional.of(testUser));

    mvc.perform(MockMvcRequestBuilders.get("/api/users/" + testUser.getId())
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value(testUser.getName()));
  }

  @Test
  public void givenNoUser_whenGetUser_thenReturnNotFoundError() throws Exception {
    given(userRepository.findById(testUser.getId())).willThrow(new NotFoundException(StatusMessages.USER_NOT_FOUND));

    mvc.perform(MockMvcRequestBuilders.get("/api/users/" + testUser.getId())
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  // GetAll

  @Test
  public void givenUserList_whenGetUsers_thenReturnJsonUserArray() throws Exception {
    ArrayList<User> userList = new ArrayList<User>();
    userList.add(testUser);
    userList.add(testUser2);
    Iterable<User> userIterable = userList;

    given(userRepository.findAll()).willReturn(userIterable);

    mvc.perform(MockMvcRequestBuilders.get("/api/users/")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].name").value(testUser.getName()))
        .andExpect(jsonPath("$[1].name").value(testUser2.getName()));
  }

  @Test
  public void givenEmptyUserList_whenGetUsers_thenReturnJsonEmptyArray() throws Exception {
    ArrayList<User> userList = new ArrayList<User>();
    Iterable<User> userIterable = userList;

    given(userRepository.findAll()).willReturn(userIterable);

    mvc.perform(MockMvcRequestBuilders.get("/api/users/")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)));
  }

  // Create User

  @Test
  public void givenUser_whenPostUsers_thenReturnJsonUserObject() throws Exception {
    given(userRepository.save(any(User.class))).willReturn(testUser);

    mvc.perform(MockMvcRequestBuilders.post("/api/users/")
        .contentType(MediaType.APPLICATION_JSON)
        .content(jsonUser.toString()))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value(testUser.getName()));
  }

  // Edit User

  @Test
  public void givenUser_whenPutUsers_thenReturnJsonUserObject() throws Exception {
    given(userRepository.save(any(User.class))).willReturn(testUser);
    given(userRepository.findById(testUser.getId())).willReturn(java.util.Optional.ofNullable(testUser));

    mvc.perform(MockMvcRequestBuilders.put("/api/users/"+testUser.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .content(jsonUser.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value(testUser.getName()));
  }

  @Test
  public void givenUnknownUser_whenPutUsers_thenReturnNotFoundError() throws Exception {
    mvc.perform(MockMvcRequestBuilders.put("/api/users/"+testUser.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .content(jsonUser.toString()))
        .andExpect(status().isNotFound());
  }

  @Test
  public void givenWrongUser_whenPutUsers_thenReturnForbiddenError() throws Exception {
    given(userRepository.findById(testUser.getId())).willReturn(java.util.Optional.ofNullable(testUser));
    JSONObject jsonWrongUser = new JSONObject(jsonUser.toString());
    jsonWrongUser.put("id", 99);

    mvc.perform(MockMvcRequestBuilders.put("/api/users/"+testUser.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .content(jsonWrongUser.toString()))
        .andExpect(status().isForbidden());
  }

  // Delete User

  @Test
  public void whenDeleteUsers_thenReturnOkNoContent() throws Exception {
    given(userRepository.findById(testUser.getId())).willReturn(java.util.Optional.ofNullable(testUser));

    mvc.perform(MockMvcRequestBuilders.delete("/api/users/"+testUser.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .content(jsonUser.toString()))
        .andExpect(status().isNoContent());
  }

  @Test
  public void givenUnknownUser_whenDeleteUsers_thenReturnNotFoundError() throws Exception {
    mvc.perform(MockMvcRequestBuilders.delete("/api/users/"+testUser.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .content(jsonUser.toString()))
        .andExpect(status().isNotFound());
  }

  // Add book

  @Test
  public void givenUserAndBook_whenAddBookToUsers_thenReturnJSONObject() throws Exception {
    given(userRepository.findById(testUser.getId())).willReturn(java.util.Optional.ofNullable(testUser));
    given(bookRepository.findById(testBook.getId())).willReturn(java.util.Optional.ofNullable(testBook));
    given(userRepository.save(any(User.class))).willReturn(testUser);

    mvc.perform(MockMvcRequestBuilders.put("/api/users/"+testUser.getId()+"/books/add")
        .contentType(MediaType.APPLICATION_JSON)
        .content(jsonBook.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value(testUser.getName()))
        .andExpect(jsonPath("$.books[0].title").value(testBook.getTitle()));
  }

  @Test
  public void givenJustBook_whenAddBookToUsers_thenReturnNotFoundError() throws Exception {
    given(userRepository.findById(testUser.getId())).willReturn(java.util.Optional.ofNullable(testUser));
    given(userRepository.save(any(User.class))).willReturn(testUser);

    mvc.perform(MockMvcRequestBuilders.put("/api/users/"+testUser.getId()+"/books/add")
        .contentType(MediaType.APPLICATION_JSON)
        .content(jsonBook.toString()))
        .andExpect(status().isNotFound());
  }

  @Test
  public void givenJustUser_whenAddBookToUsers_thenReturnNotFoundError() throws Exception {
    given(bookRepository.findById(testBook.getId())).willReturn(java.util.Optional.ofNullable(testBook));

    mvc.perform(MockMvcRequestBuilders.put("/api/users/"+testUser.getId()+"/books/add")
        .contentType(MediaType.APPLICATION_JSON)
        .content(jsonBook.toString()))
        .andExpect(status().isNotFound());
  }

  @Test
  public void givenUserWithBookAndBook_whenAddBookToUsers_thenReturnJSONObject() throws Exception {
    given(userRepository.findById(testUser.getId())).willReturn(java.util.Optional.ofNullable(testUser));
    given(bookRepository.findById(testBook.getId())).willReturn(java.util.Optional.ofNullable(testBook));
    given(userRepository.save(any(User.class))).willReturn(testUser);

    testUser.addBook(testBook);

    mvc.perform(MockMvcRequestBuilders.put("/api/users/"+testUser.getId()+"/books/add")
        .contentType(MediaType.APPLICATION_JSON)
        .content(jsonBook.toString()))
        .andExpect(status().isConflict());
  }

  // Remove book

  @Test
  public void givenUserAndBook_whenDeleteBookToUsers_thenReturnJSONObject() throws Exception {
    given(userRepository.findById(testUser.getId())).willReturn(java.util.Optional.ofNullable(testUser));
    given(bookRepository.findById(testBook.getId())).willReturn(java.util.Optional.ofNullable(testBook));
    given(userRepository.save(any(User.class))).willReturn(testUser);

    testUser.addBook(testBook);

    mvc.perform(MockMvcRequestBuilders.put("/api/users/"+testUser.getId()+"/books/remove")
        .contentType(MediaType.APPLICATION_JSON)
        .content(jsonBook.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value(testUser.getName()))
        .andExpect(jsonPath("$.books", hasSize(0)));
  }

  @Test
  public void givenJustBook_whenDeleteBookToUsers_thenReturnNotFoundError() throws Exception {
    given(bookRepository.findById(testBook.getId())).willReturn(java.util.Optional.ofNullable(testBook));

    testUser.addBook(testBook);

    mvc.perform(MockMvcRequestBuilders.put("/api/users/"+testUser.getId()+"/books/remove")
        .contentType(MediaType.APPLICATION_JSON)
        .content(jsonBook.toString()))
        .andExpect(status().isNotFound());
  }

  @Test
  public void givenJustUser_whenDeleteBookToUsers_thenReturnNotFoundError() throws Exception {
    given(userRepository.findById(testUser.getId())).willReturn(java.util.Optional.ofNullable(testUser));
    given(userRepository.save(any(User.class))).willReturn(testUser);

    testUser.addBook(testBook);

    mvc.perform(MockMvcRequestBuilders.put("/api/users/"+testUser.getId()+"/books/remove")
        .contentType(MediaType.APPLICATION_JSON)
        .content(jsonBook.toString()))
        .andExpect(status().isNotFound());
  }

  @Test
  public void givenUserWithoutBookAndBook_whenDeleteBookToUsers_thenReturnJSONObject() throws Exception {
    given(userRepository.findById(testUser.getId())).willReturn(java.util.Optional.ofNullable(testUser));
    given(bookRepository.findById(testBook.getId())).willReturn(java.util.Optional.ofNullable(testBook));
    given(userRepository.save(any(User.class))).willReturn(testUser);

    mvc.perform(MockMvcRequestBuilders.put("/api/users/"+testUser.getId()+"/books/remove")
        .contentType(MediaType.APPLICATION_JSON)
        .content(jsonBook.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value(testUser.getName()))
        .andExpect(jsonPath("$.books", hasSize(0)));
  }
}
