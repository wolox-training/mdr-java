package wolox.training.controllers;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import wolox.training.exceptions.Forbidden;
import wolox.training.exceptions.NotFound;
import wolox.training.models.Book;
import wolox.training.repositories.BookRepository;

@RestController
@RequestMapping("/api/books")
public class BookController {

  @Autowired
  private BookRepository bookRepository;

  @GetMapping("/greeting")
  public String greeting(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
    model.addAttribute("name", name);
    return "greeting";
  }

  @GetMapping("/{id}")
  public Optional<Book> read(@PathVariable Long id) {
    Optional<Book> book = bookRepository.findById(id);
    if (!book.isPresent()) throw new NotFound("Book not found");
    return book;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Book create(@RequestBody Book book) {
    return bookRepository.save(book);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id) {
    Optional<Book> book = bookRepository.findById(id);
    if (!book.isPresent()) throw new NotFound("Book not found");
    bookRepository.deleteById(id);
  }

  @PutMapping("/{id}")
  public Book update(@RequestBody Book book, @PathVariable Long id) {
    Optional<Book> foundBook = bookRepository.findById(id);
    if (!foundBook.isPresent()) throw new NotFound("Book not found");
    if (book.getId() != id) {
      throw new Forbidden( "Cannot change 'id'");
    }
    return bookRepository.save(book);
  }

}