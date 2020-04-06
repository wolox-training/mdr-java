package wolox.training.repositories;

import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import wolox.training.models.User;

public interface UserRepository extends CrudRepository<User, Long> {
  Optional<User> findFirstByUsername(String username);

  Iterable<User> findAllByBirthdateBetweenAndNameContainingIgnoreCase(LocalDate birthdateStart, LocalDate birthdateEnd, String name);
}
