package wolox.training.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;
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
  Page<Book> findAllByPublisherAndGenreAndYear(
      @Param("publisher") String publisher,
      @Param("genre") String genre,
      @Param("year") String year,
      Pageable pageable
  );

  @Query("SELECT b FROM Book b" +
      " WHERE (:genre = '' OR LOWER(b.genre) LIKE LOWER(CONCAT('%',:genre,'%')))" +
      " AND (:author = '' OR LOWER(b.author) LIKE LOWER(CONCAT('%',:author,'%')))" +
      " AND (:image = '' OR LOWER(b.image) LIKE LOWER(CONCAT('%',:image,'%')))" +
      " AND (:title = '' OR LOWER(b.title) LIKE LOWER(CONCAT('%',:title,'%')))" +
      " AND (:subtitle = '' OR LOWER(b.subtitle) LIKE LOWER(CONCAT('%',:subtitle,'%')))" +
      " AND (:publisher = '' OR LOWER(b.publisher) LIKE LOWER(CONCAT('%',:publisher,'%')))" +
      " AND (:year = '' OR b.year = :year)" +
      " AND (:pages = '' OR b.pages = :pages)" +
      " AND (:isbn = '' OR LOWER(b.isbn) LIKE LOWER(CONCAT('%',:isbn,'%')))")
  Page<Book> findAllWithFilters(
      @Param("genre") String genre,
      @Param("author") String author,
      @Param("image") String image,
      @Param("title")String title,
      @Param("subtitle")String subtitle,
      @Param("publisher")String publisher,
      @Param("year")String year,
      @Param("pages")String minPages,
      @Param("isbn")String isbn,
      Pageable pageable
  );

  Page<Book> findAll(Pageable pageable);
}
