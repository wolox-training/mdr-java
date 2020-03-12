package wolox.training.repositories;

import java.util.Optional;
import javax.persistence.Id;
import org.springframework.data.repository.CrudRepository;
import wolox.training.models.Book;

public interface BookRepository extends CrudRepository<Book, Id> {
  Optional<Book> findFirstByAuthor(String author);
}
