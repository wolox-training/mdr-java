package wolox.training.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import wolox.training.constants.StatusMessages;
import wolox.training.exceptions.NotFoundException;
import wolox.training.models.Book;

import java.util.Objects;

@Service
public class OpenLibraryService {

  private final String base_url = "https://openlibrary.org/api/books";

  private RestTemplate restTemplate = new RestTemplate();

  private ObjectMapper objectMapper = new ObjectMapper();

  public Book bookInfo(String isbn) throws JsonProcessingException {
    String url = UriComponentsBuilder
        .fromHttpUrl(base_url)
        .queryParam("bibkeys", "ISBN:" + isbn)
        .queryParam("format","json")
        .queryParam("jscmd","data")
        .toUriString();
    ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
    JsonNode jsonNode = objectMapper.readTree(Objects.requireNonNull(response.getBody()));
    JsonNode book = jsonNode.get("ISBN:" + isbn);
    if (book == null) throw new NotFoundException(StatusMessages.BOOK_NOT_FOUND);
    return new Book(book.get("authors").toString(), "", book.get("title").asText(), book.get("subtitle").asText(),book.get("publishers").toString(), book.get("publish_date").asText(), book.get("number_of_pages").asText(),isbn, "");
  }
}
