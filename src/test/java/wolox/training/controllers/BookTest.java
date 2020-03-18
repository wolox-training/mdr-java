package wolox.training.controllers;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import wolox.training.models.Book;
import wolox.training.repositories.BookRepository;

import java.util.ArrayList;
import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@WebMvcTest(BookController.class)
class BookControllerIntegrationTest {

  @Autowired
  private MockMvc mvc;

  @MockBean
  private BookRepository bookRepository;

  // write test cases here

  private Book testBook, testBook2;
  private JSONObject jsonBook;



  @BeforeEach
  public void createVariables() {
    try {
      testBook = new Book("Doyle","image","title","subtitle","publisher","1999","500","isbn","terror");
      testBook2 = new Book("Doyle2","image2","title2","subtitle2","publisher2","1998","800","isbn2","fiction");
      jsonBook = new JSONObject()
          .put("author", testBook.getAuthor())
          .put("image", testBook.getImage())
          .put("title", testBook.getTitle())
          .put("subtitle",testBook.getSubtitle())
          .put("publisher", testBook.getPublisher())
          .put("year", testBook.getYear())
          .put("pages", testBook.getPages())
          .put("isbn", testBook.getIsbn())
          .put("genre", testBook.getGenre());
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  // GetById

  @Test
  public void givenBook_whenGetBook_thenReturnJsonBookObject() throws Exception {
    given(bookRepository.findById(testBook.getId())).willReturn(java.util.Optional.of(testBook));

    mvc.perform(MockMvcRequestBuilders.get("/api/books/" + testBook.getId())
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title").value(testBook.getTitle()));
  }

  @Test
  public void givenNoBook_whenGetBook_thenReturnNotFoundError() throws Exception {
      mvc.perform(MockMvcRequestBuilders.get("/api/books/" + testBook.getId())
          .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isNotFound());
    }

  // GetAll

  @Test
  public void givenBookList_whenGetBooks_thenReturnJsonBooksArray() throws Exception {
    given(bookRepository.findAll()).willReturn(Arrays.asList(testBook, testBook2));

    mvc.perform(MockMvcRequestBuilders.get("/api/books/")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].title").value(testBook.getTitle()))
        .andExpect(jsonPath("$[1].title").value(testBook2.getTitle()));
  }

  @Test
  public void givenEmptyBookList_whenGetBooks_thenReturnJsonEmptyArray() throws Exception {
    given(bookRepository.findAll()).willReturn(new ArrayList<Book>());

    mvc.perform(MockMvcRequestBuilders.get("/api/books/")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)));
  }

  // Create Book

  @Test
  public void givenBook_whenPostBooks_thenReturnJsonBookObject() throws Exception {
    given(bookRepository.save(any(Book.class))).willReturn(testBook);

    mvc.perform(MockMvcRequestBuilders.post("/api/books/")
        .contentType(MediaType.APPLICATION_JSON)
        .content(jsonBook.toString()))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.title").value(testBook.getTitle()));
  }

  // Edit Book

  @Test
  public void givenBook_whenPutBooks_thenReturnJsonBookObject() throws Exception {
    given(bookRepository.save(any(Book.class))).willReturn(testBook);
    given(bookRepository.findById(testBook.getId())).willReturn(java.util.Optional.ofNullable(testBook));

    mvc.perform(MockMvcRequestBuilders.put("/api/books/"+testBook.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .content(jsonBook.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title").value(testBook.getTitle()));
  }

  @Test
  public void givenUnknownBook_whenPutBook_thenReturnNotFoundError() throws Exception {
    mvc.perform(MockMvcRequestBuilders.put("/api/books/"+testBook.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .content(jsonBook.toString()))
        .andExpect(status().isNotFound());
  }

  @Test
  public void givenWrongBook_whenPutBooks_thenReturnForbiddenError() throws Exception {
    given(bookRepository.findById(testBook.getId())).willReturn(java.util.Optional.ofNullable(testBook));
    jsonBook.put("id", 99);

    mvc.perform(MockMvcRequestBuilders.put("/api/books/"+testBook.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .content(jsonBook.toString()))
        .andExpect(status().isForbidden());
  }

  // Delete Book

  @Test
  public void whenDeleteBooks_thenReturnOkNoContent() throws Exception {
    given(bookRepository.findById(testBook.getId())).willReturn(java.util.Optional.ofNullable(testBook));

    mvc.perform(MockMvcRequestBuilders.delete("/api/books/"+testBook.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .content(jsonBook.toString()))
        .andExpect(status().isNoContent());
  }

  @Test
  public void givenUnknownBook_whenDeleteBooks_thenReturnNotFoundError() throws Exception {
    mvc.perform(MockMvcRequestBuilders.delete("/api/books/"+testBook.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .content(jsonBook.toString()))
        .andExpect(status().isNotFound());
  }
}
