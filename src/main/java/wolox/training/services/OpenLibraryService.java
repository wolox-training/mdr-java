package wolox.training.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import wolox.training.models.Book;

import java.util.Objects;
import java.util.Optional;

@Service
public class OpenLibraryService {

  private final String BASE_URL = "https://openlibrary.org/api/books";

  private RestTemplate restTemplate = new RestTemplate();

  private ObjectMapper objectMapper = new ObjectMapper();

  public Optional<Book> bookInfo(String isbn) throws JsonProcessingException {
    String url = UriComponentsBuilder
        .fromHttpUrl(BASE_URL)
        .queryParam("bibkeys", "ISBN:" + isbn)
        .queryParam("format","json")
        .queryParam("jscmd","data")
        .toUriString();
    ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
    JsonNode jsonNode = objectMapper.readTree(Objects.requireNonNull(response.getBody()));
    JsonNode book = jsonNode.get("ISBN:" + isbn);
    if (book == null) return Optional.empty();
    Book newBook = new Book(
        book.get("authors").toString(),
        book.get("cover").toString(),
        book.get("title").asText(),
        book.get("subtitle").asText(),
        book.get("publishers").toString(),
        book.get("publish_date").asText(),
        book.get("number_of_pages").asText(),
        isbn,
        "");
    return Optional.of(newBook);
  }
}
