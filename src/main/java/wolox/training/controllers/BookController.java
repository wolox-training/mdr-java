package wolox.training.controllers;

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
import wolox.training.exceptions.ForbiddenException;
import wolox.training.exceptions.NotFoundException;
import wolox.training.models.Book;
import wolox.training.repositories.BookRepository;
import wolox.training.constants.StatusMessages;

import java.util.List;

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
  public Book read(@PathVariable Long id) {
    return bookRepository.findById(id).orElseThrow(() -> new NotFoundException(StatusMessages.BOOK_NOT_FOUND));
  }

  @GetMapping
  public List<Book> readAll() {
    return (List<Book>) bookRepository.findAll();
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