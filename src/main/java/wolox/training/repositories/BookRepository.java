package wolox.training.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import wolox.training.models.Book;

public interface BookRepository extends CrudRepository<Book, Long> {
  Optional<Book> findFirstByAuthor(String author);

  Optional<Book> findById(Long id);

  void deleteById(Long id);

  Optional<Book> findFirstByIsbn(String isbn);

  @Query("SELECT b FROM Book b" +
      " WHERE (:publisher is null OR b.publisher = :publisher)" +
      " AND (:genre is null OR b.genre = :genre)" +
      " AND (:year is null OR b.year = :year)")
  List<Book> findAllByPublisherAndGenreAndYear(@Param("publisher") String publisher, @Param("genre") String genre, @Param("year") String year);
}
