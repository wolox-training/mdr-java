package wolox.training.repositories;

import javax.persistence.Id;
import org.springframework.data.jpa.repository.JpaRepository;
import wolox.training.models.Book;

public interface BookRepository extends JpaRepository<Book, Id> {
  Book findFirstByAuthor(String author);
}
