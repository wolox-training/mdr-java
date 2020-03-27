package wolox.training.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import wolox.training.exceptions.ForbiddenException;
import wolox.training.exceptions.NotFoundException;
import wolox.training.models.Book;
import wolox.training.repositories.BookRepository;
import wolox.training.constants.StatusMessages;
import wolox.training.services.OpenLibraryService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/books")
public class BookController {

  @Autowired
  private OpenLibraryService libraryService;

  @Autowired
  private BookRepository bookRepository;

  @GetMapping("/greeting")
  public String greeting(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
    model.addAttribute("name", name);
    return "greeting";
  }

  @GetMapping("/{id}")
  public Book read(@PathVariable Long id) {
    return bookRepository.findById(id).orElseThrow(() -> new NotFoundException(StatusMessages.BOOK_NOT_FOUND));
  }

  @GetMapping("/isbn/{isbn}")
  public ResponseEntity<?> searchByIsbn(@PathVariable String isbn) {
    Optional<Book> internalBook = bookRepository.findFirstByIsbn(isbn);
    if (internalBook.isPresent()) return new ResponseEntity<>(internalBook,HttpStatus.OK);
    try {
      Book book = libraryService.bookInfo(isbn).orElseThrow(() -> new NotFoundException(StatusMessages.BOOK_NOT_FOUND));
      return new ResponseEntity<>(bookRepository.save(book), HttpStatus.CREATED);
    } catch (JsonProcessingException e) {
      // In case of json parsing error, it's considered as a book not found error
      throw new NotFoundException(StatusMessages.BOOK_NOT_FOUND);
    }
  }

  @GetMapping
  public Page<Book> readAll(Pageable pageable) {
    return bookRepository.findAll(pageable);
  }

  @GetMapping("/search")
  public Page<Book> readAllByPublisherAndGenreAndYear(
      @RequestParam(required=false) String publisher,
      @RequestParam(required=false) String genre,
      @RequestParam(required=false) String year,
      Pageable pageable) {
    return bookRepository.findAllByPublisherAndGenreAndYear(publisher, genre, year, pageable);
  }

  @GetMapping("/search-by")
  public Page<Book> readAllWithFilters(
      @RequestParam(required=false, defaultValue = "") String genre,
      @RequestParam(required=false, defaultValue = "") String author,
      @RequestParam(required=false, defaultValue = "") String image,
      @RequestParam(required=false, defaultValue = "") String title,
      @RequestParam(required=false, defaultValue = "") String subtitle,
      @RequestParam(required=false, defaultValue = "") String publisher,
      @RequestParam(required=false, defaultValue = "") String year,
      @RequestParam(required=false, defaultValue = "") String pages,
      @RequestParam(required=false, defaultValue = "") String isbn,
      Pageable pageable) {
    return bookRepository.findAllWithFilters(genre, author, image, title, subtitle, publisher, year, pages, isbn, pageable);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Book create(@RequestBody Book book) {
    return bookRepository.save(book);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(code = HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    bookRepository.findById(id).orElseThrow(() -> new NotFoundException(StatusMessages.BOOK_NOT_FOUND));
    bookRepository.deleteById(id);
  }

  @PutMapping("/{id}")
  public Book update(@RequestBody Book book, @PathVariable Long id) {
    bookRepository.findById(id).orElseThrow(() -> new NotFoundException(StatusMessages.BOOK_NOT_FOUND));
    if (!book.getId().equals(id)) throw new ForbiddenException(StatusMessages.CANNOT_CHANGE_ID);
    return bookRepository.save(book);
  }

}