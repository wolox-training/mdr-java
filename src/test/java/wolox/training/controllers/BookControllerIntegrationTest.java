package wolox.training.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import wolox.training.components.CustomAuthenticationProvider;
import wolox.training.models.Book;
import wolox.training.repositories.BookRepository;
import wolox.training.services.OpenLibraryService;

import java.util.ArrayList;
import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.mockito.BDDMockito.given;

@WebMvcTest(BookController.class)
class BookControllerIntegrationTest {

  @Autowired
  private MockMvc mvc;

  @MockBean
  private BookRepository bookRepository;

  @MockBean
  private CustomAuthenticationProvider customAuthenticationProvider;

  @MockBean
  private OpenLibraryService openLibraryService;

  // write test cases here

  private Book testBook, testBook2;
  private String jsonBook;



  @BeforeEach
  public void createVariables() {
    testBook = new Book("Doyle","image","title","subtitle","publisher","1999","500","isbn","terror");
    testBook2 = new Book("Doyle2","image2","title2","subtitle2","publisher2","1998","800","isbn2","fiction");
    jsonBook = "{\"author\": \"" + testBook.getAuthor() + "\"," +
        "\"image\": \"" + testBook.getImage() + "\"," +
        "\"title\": \"" + testBook.getTitle() + "\"," +
        "\"subtitle\": \"" + testBook.getSubtitle() + "\"," +
        "\"publisher\": \"" + testBook.getPublisher() + "\"," +
        "\"year\": \"" + testBook.getYear() + "\"," +
        "\"pages\": \"" + testBook.getPages() + "\"," +
        "\"isbn\": \"" + testBook.getIsbn() + "\"," +
        "\"genre\": \"" + testBook.getGenre() + "\"" +
        "}";
  }

  // GetById

  @WithMockUser(value = "test")
  @Test
  public void givenBook_whenGetBook_thenReturnJsonBookObject() throws Exception {
    given(bookRepository.findById(testBook.getId())).willReturn(java.util.Optional.of(testBook));

    mvc.perform(MockMvcRequestBuilders.get("/api/books/" + testBook.getId())
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title").value(testBook.getTitle()));
  }

  @WithMockUser(value = "test")
  @Test
  public void givenNoBook_whenGetBook_thenReturnNotFoundError() throws Exception {
      mvc.perform(MockMvcRequestBuilders.get("/api/books/" + testBook.getId())
          .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isNotFound());
    }

  // GetAll

  @WithMockUser(value = "test")
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

  @WithMockUser(value = "test")
  @Test
  public void givenEmptyBookList_whenGetBooks_thenReturnJsonEmptyArray() throws Exception {
    given(bookRepository.findAll()).willReturn(new ArrayList<Book>());

    mvc.perform(MockMvcRequestBuilders.get("/api/books/")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)));
  }

  // Search

  @WithMockUser(value = "test")
  @Test
  public void givenBookList_whenSearchBooksWith3Params_thenReturnJsonBooksArray() throws Exception {
    given(bookRepository.findAllByPublisherAndGenreAndYear(testBook.getPublisher(),testBook.getGenre(),testBook.getYear()))
        .willReturn(Arrays.asList(testBook));

    mvc.perform(MockMvcRequestBuilders.get("/api/books/search")
        .contentType(MediaType.APPLICATION_JSON)
        .queryParam("publisher",testBook.getPublisher())
        .queryParam("year",testBook.getYear())
        .queryParam("genre",testBook.getGenre()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].title").value(testBook.getTitle()));
  }

  @WithMockUser(value = "test")
  @Test
  public void givenBookList_whenSearchBooksWith2Params_thenReturnJsonBooksArray() throws Exception {
    given(bookRepository.findAllByPublisherAndGenreAndYear(testBook.getPublisher(),testBook.getGenre(),null))
        .willReturn(Arrays.asList(testBook));

    mvc.perform(MockMvcRequestBuilders.get("/api/books/search")
        .contentType(MediaType.APPLICATION_JSON)
        .queryParam("publisher",testBook.getPublisher())
        .queryParam("genre",testBook.getGenre()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].title").value(testBook.getTitle()));
  }

  @WithMockUser(value = "test")
  @Test
  public void givenEmptyBookList_whenSearchBooks_thenReturnJsonEmptyArray() throws Exception {
    given(bookRepository.findAllByPublisherAndGenreAndYear(testBook.getPublisher(),testBook.getGenre(),testBook.getYear()))
        .willReturn(Arrays.asList());

    mvc.perform(MockMvcRequestBuilders.get("/api/books/search")
        .contentType(MediaType.APPLICATION_JSON)
        .queryParam("publisher",testBook.getPublisher())
        .queryParam("year",testBook.getYear())
        .queryParam("genre",testBook.getGenre()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)));
  }

  // Create Book

  @WithMockUser(value = "test")
  @Test
  public void givenBook_whenPostBooks_thenReturnJsonBookObject() throws Exception {
    given(bookRepository.save(any(Book.class))).willReturn(testBook);

    mvc.perform(MockMvcRequestBuilders.post("/api/books/")
        .contentType(MediaType.APPLICATION_JSON)
        .content(jsonBook))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.title").value(testBook.getTitle()));
  }

  // Edit Book

  @WithMockUser(value = "test")
  @Test
  public void givenBook_whenPutBooks_thenReturnJsonBookObject() throws Exception {
    given(bookRepository.save(any(Book.class))).willReturn(testBook);
    given(bookRepository.findById(testBook.getId())).willReturn(java.util.Optional.ofNullable(testBook));

    mvc.perform(MockMvcRequestBuilders.put("/api/books/"+testBook.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .content(jsonBook))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title").value(testBook.getTitle()));
  }

  @WithMockUser(value = "test")
  @Test
  public void givenUnknownBook_whenPutBook_thenReturnNotFoundError() throws Exception {
    mvc.perform(MockMvcRequestBuilders.put("/api/books/"+testBook.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .content(jsonBook.toString()))
        .andExpect(status().isNotFound());
  }

  @WithMockUser(value = "test")
  @Test
  public void givenWrongBook_whenPutBooks_thenReturnForbiddenError() throws Exception {
    given(bookRepository.findById(testBook.getId())).willReturn(java.util.Optional.ofNullable(testBook));
    jsonBook = "{\"author\": \"" + testBook.getAuthor() + "\"," +
        "\"image\": \"" + testBook.getImage() + "\"," +
        "\"title\": \"" + testBook.getTitle() + "\"," +
        "\"subtitle\": \"" + testBook.getSubtitle() + "\"," +
        "\"publisher\": \"" + testBook.getPublisher() + "\"," +
        "\"year\": \"" + testBook.getYear() + "\"," +
        "\"pages\": \"" + testBook.getPages() + "\"," +
        "\"isbn\": \"" + testBook.getIsbn() + "\"," +
        "\"genre\": \"" + testBook.getGenre() + "\"," +
        "\"id\": 99" +
        "}";

    mvc.perform(MockMvcRequestBuilders.put("/api/books/"+testBook.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .content(jsonBook))
        .andExpect(status().isForbidden());
  }

  // Delete Book

  @WithMockUser(value = "test")
  @Test
  public void whenDeleteBooks_thenReturnOkNoContent() throws Exception {
    given(bookRepository.findById(testBook.getId())).willReturn(java.util.Optional.ofNullable(testBook));

    mvc.perform(MockMvcRequestBuilders.delete("/api/books/"+testBook.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .content(jsonBook))
        .andExpect(status().isNoContent());
  }

  @WithMockUser(value = "test")
  @Test
  public void givenUnknownBook_whenDeleteBooks_thenReturnNotFoundError() throws Exception {
    mvc.perform(MockMvcRequestBuilders.delete("/api/books/"+testBook.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .content(jsonBook))
        .andExpect(status().isNotFound());
  }
}
