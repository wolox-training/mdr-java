package wolox.training.controllers;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import wolox.training.components.CustomAuthenticationProvider;
import wolox.training.models.Book;
import wolox.training.repositories.BookRepository;
import wolox.training.services.OpenLibraryService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.BDDMockito.given;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

@WebMvcTest(BookController.class)
@Import(OpenLibraryService.class)
class BookControllerIntegrationTest {

  @Autowired
  private MockMvc mvc;

  @MockBean
  private BookRepository bookRepository;

  @MockBean
  private CustomAuthenticationProvider customAuthenticationProvider;

  // write test cases here

  private Book testBook, testBook2;
  private String jsonBook;
  private Pageable pageable;
  private static final String externalIsbn = "12345678";
  private static WireMockServer wireMockServer;

  @BeforeAll
  public static void beforeAll() {
    wireMockServer = new WireMockServer(options().bindAddress("localhost").port(8888).usingFilesUnderDirectory("src/test/java/wolox/training/mocks"));
    wireMockServer.stubFor(get(urlMatching("/api/books.*"))
        .atPriority(5)
        .willReturn(aResponse()
            .withHeader("Content-Type", "application/json")
            .withBodyFile("response_empty_book")));
    wireMockServer.stubFor(get(urlMatching("/api/books.*"))
        .withQueryParam("bibkeys", equalTo("ISBN:" + externalIsbn))
        .withQueryParam("format",equalTo("json"))
        .withQueryParam("jscmd",equalTo("data"))
        .atPriority(1)
        .willReturn(aResponse()
            .withHeader("Content-Type", "application/json")
            .withBodyFile("response_ok_book")));

    wireMockServer.start();
  }

  @AfterAll
  public static void afterAll() {
    wireMockServer.stop();
  }

  @BeforeEach
  public void beforeEach() {
    testBook = new Book("Doyle","image","title","subtitle","publisher","1999","500", externalIsbn,"terror");
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
    pageable = PageRequest.of(0, 20);
  }

  // GetByIsbn

  @WithMockUser(value = "test")
  @Test
  public void givenExternalBook_whenGetBookByIsbn_thenCreateBookAndReturnJsonBookObject() throws Exception {
    given(bookRepository.findFirstByIsbn(testBook.getIsbn())).willReturn(java.util.Optional.empty());
    given(bookRepository.save(any(Book.class))).willReturn(testBook);

    mvc.perform(MockMvcRequestBuilders.get("/api/books/isbn/" + testBook.getIsbn())
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.title").value(testBook.getTitle()));
  }

  @WithMockUser(value = "test")
  @Test
  public void givenNoExternalBook_whenGetBookByIsbn_thenCreateBookAndReturnJsonBookObject() throws Exception {
    given(bookRepository.findFirstByIsbn(testBook.getIsbn())).willReturn(java.util.Optional.empty());

    mvc.perform(MockMvcRequestBuilders.get("/api/books/isbn/987654321")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @WithMockUser(value = "test")
  @Test
  public void givenExternalBook_whenGetBookByIsbn_thenReturnJsonBookObject() throws Exception {
    given(bookRepository.findFirstByIsbn(testBook.getIsbn())).willReturn(java.util.Optional.of(testBook));

    mvc.perform(MockMvcRequestBuilders.get("/api/books/isbn/" + testBook.getIsbn())
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title").value(testBook.getTitle()));
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
    List<Book> bookList = Arrays.asList(testBook, testBook2);
    Page<Book> page = new PageImpl<>(bookList);
    given(bookRepository.findAll(pageable)).willReturn(page);

    mvc.perform(MockMvcRequestBuilders.get("/api/books/")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalElements").value(2))
        .andExpect(jsonPath("$.content[0].title").value(testBook.getTitle()))
        .andExpect(jsonPath("$.content[1].title").value(testBook2.getTitle()));
  }

  @WithMockUser(value = "test")
  @Test
  public void givenEmptyBookList_whenGetBooks_thenReturnJsonEmptyArray() throws Exception {
    List<Book> bookList = Collections.emptyList();
    Page<Book> page = new PageImpl<>(bookList);

    given(bookRepository.findAll(pageable)).willReturn(page);

    mvc.perform(MockMvcRequestBuilders.get("/api/books/")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalElements").value(0));
  }

  // Search

  @WithMockUser(value = "test")
  @Test
  public void givenBookList_whenSearchBooksWithAllParams_thenReturnJsonBooksArray() throws Exception {
    List<Book> bookList = Collections.singletonList(testBook);
    Page<Book> page = new PageImpl<>(bookList);

    given(bookRepository.findAllByPublisherAndGenreAndYear(testBook.getPublisher(),testBook.getGenre(),testBook.getYear(),pageable))
        .willReturn(page);

    mvc.perform(MockMvcRequestBuilders.get("/api/books/search")
        .contentType(MediaType.APPLICATION_JSON)
        .queryParam("publisher",testBook.getPublisher())
        .queryParam("year",testBook.getYear())
        .queryParam("genre",testBook.getGenre()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalElements").value(1))
        .andExpect(jsonPath("$.content[0].title").value(testBook.getTitle()));
  }

  @WithMockUser(value = "test")
  @Test
  public void givenBookList_whenSearchBooksWithNullParams_thenReturnJsonBooksArray() throws Exception {
    List<Book> bookList = Collections.singletonList(testBook);
    Page<Book> page = new PageImpl<>(bookList);

    given(bookRepository.findAllByPublisherAndGenreAndYear(testBook.getPublisher(),testBook.getGenre(),null,pageable))
        .willReturn(page);

    mvc.perform(MockMvcRequestBuilders.get("/api/books/search")
        .contentType(MediaType.APPLICATION_JSON)
        .queryParam("publisher",testBook.getPublisher())
        .queryParam("genre",testBook.getGenre()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalElements").value(1))
        .andExpect(jsonPath("$.content[0].title").value(testBook.getTitle()));
  }

  @WithMockUser(value = "test")
  @Test
  public void givenEmptyBookList_whenSearchBooks_thenReturnJsonEmptyArray() throws Exception {
    List<Book> bookList = Collections.emptyList();
    Page<Book> page = new PageImpl<>(bookList);

    given(bookRepository.findAllByPublisherAndGenreAndYear(testBook.getPublisher(),testBook.getGenre(),testBook.getYear(),pageable))
        .willReturn(page);

    mvc.perform(MockMvcRequestBuilders.get("/api/books/search")
        .contentType(MediaType.APPLICATION_JSON)
        .queryParam("publisher",testBook.getPublisher())
        .queryParam("year",testBook.getYear())
        .queryParam("genre",testBook.getGenre()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalElements").value(0));
  }


  // Search by

  @WithMockUser(value = "test")
  @Test
  public void givenBookList_whenSearchBooksByAllParams_thenReturnJsonBooksArray() throws Exception {
    List<Book> bookList = Arrays.asList(testBook, testBook);
    Page<Book> page = new PageImpl<>(bookList);

    given(bookRepository.findAllWithFilters(
        testBook.getGenre(),
        testBook.getAuthor(),
        testBook.getImage(),
        testBook.getTitle(),
        testBook.getSubtitle(),
        testBook.getPublisher(),
        testBook.getYear(),
        testBook.getPages(),
        testBook.getIsbn(),
        pageable))
        .willReturn(page);

    mvc.perform(MockMvcRequestBuilders.get("/api/books/search-by")
        .contentType(MediaType.APPLICATION_JSON)
        .queryParam("genre",testBook.getGenre())
        .queryParam("author",testBook.getAuthor())
        .queryParam("image",testBook.getImage())
        .queryParam("title",testBook.getTitle())
        .queryParam("subtitle",testBook.getSubtitle())
        .queryParam("publisher",testBook.getPublisher())
        .queryParam("year",testBook.getYear())
        .queryParam("pages",testBook.getPages())
        .queryParam("isbn",testBook.getIsbn()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalElements").value(2))
        .andExpect(jsonPath("$.content[0].title").value(testBook.getTitle()))
        .andExpect(jsonPath("$.content[1].title").value(testBook.getTitle()));
  }

  @WithMockUser(value = "test")
  @Test
  public void givenBookList_whenSearchBooksBySomeParams_thenReturnJsonBooksArray() throws Exception {
    List<Book> bookList = Arrays.asList(testBook, testBook, testBook2);
    Page<Book> page = new PageImpl<>(bookList);

    given(bookRepository.findAllWithFilters(
        "",
        testBook.getAuthor(),
        testBook.getImage(),
        testBook.getTitle(),
        "",
        "",
        "",
        "",
        "",
        pageable))
        .willReturn(page);

    mvc.perform(MockMvcRequestBuilders.get("/api/books/search-by")
        .contentType(MediaType.APPLICATION_JSON)
        .queryParam("image",testBook.getImage())
        .queryParam("author",testBook.getAuthor())
        .queryParam("title",testBook.getTitle()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalElements").value(3))
        .andExpect(jsonPath("$.content[0].title").value(testBook.getTitle()))
        .andExpect(jsonPath("$.content[1].title").value(testBook.getTitle()))
        .andExpect(jsonPath("$.content[2].title").value(testBook2.getTitle()));
  }

  @WithMockUser(value = "test")
  @Test
  public void givenEmptyBookList_whenSearchBooksByNoneParams_thenReturnJsonEmptyArray() throws Exception {
    List<Book> bookList = Collections.emptyList();
    Page<Book> page = new PageImpl<>(bookList);

    given(bookRepository.findAllWithFilters(
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        pageable))
        .willReturn(page);

    mvc.perform(MockMvcRequestBuilders.get("/api/books/search-by")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalElements").value(0));
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
