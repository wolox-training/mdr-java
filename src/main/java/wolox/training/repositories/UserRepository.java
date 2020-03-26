package wolox.training.repositories;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import wolox.training.models.User;

public interface UserRepository extends CrudRepository<User, Long> {
  Optional<User> findFirstByUsername(String username);

  @Query("SELECT u FROM User u" +
      " WHERE (cast(:birthdate_start as date) is null OR u.birthdate >= :birthdate_start)" +
      " AND (cast(:birthdate_end as date) is null OR u.birthdate <= :birthdate_end)" +
      " AND (:name is null OR u.name LIKE CONCAT('%',:name,'%'))")
  Iterable<User> findAllByBirthdateAndName(
      @Param("birthdate_start") LocalDate birthdateStart,
      @Param("birthdate_end")  LocalDate birthdateEnd,
      @Param("name")  String name);
}
