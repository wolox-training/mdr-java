package wolox.training.repositories;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;
import wolox.training.models.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
class UserRepositoryTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private UserRepository userRepository;

  // write test cases here

  @Test
  public void whenFindByUsername_thenReturnUserObject() {
    User testUser = new User("username","test name", LocalDate.of(1992, 02, 02), "encodedPassword");
    entityManager.persist(testUser);
    entityManager.flush();

    Optional<User> found = userRepository.findFirstByUsername(testUser.getUsername());

    assertThat(found.get().getUsername())
        .isEqualTo(testUser.getUsername());
  }

  @Test
  public void whenFindByUsername_thenReturnNull() {
    User testUser = new User("username","test name", LocalDate.of(1992, 02, 02), "encodedPassword");
    entityManager.persist(testUser);
    entityManager.flush();

    Optional<User> found = userRepository.findFirstByUsername(testUser.getUsername() + "notMatch");

    assertThat(found.isPresent()).isFalse();
  }

  @Test
  public void whenFindAllByBirthdateBetweenAndNameContainingIgnoreCase_thenReturnUsers() {
    User testUser = new User("username","good name", LocalDate.of(1992, 02, 05), "encodedPassword");
    User testUser2 = new User("username2","good name 2", LocalDate.of(1992, 05, 01), "encodedPassword");
    User testUser3 = new User("username3","bad name", LocalDate.of(1992, 02, 15), "encodedPassword");
    entityManager.persist(testUser);
    entityManager.persist(testUser2);
    entityManager.persist(testUser3);
    entityManager.flush();

    ArrayList<User> users = (ArrayList<User>) userRepository.findAllByBirthdateBetweenAndNameContainingIgnoreCase(
        LocalDate.of(1992, 02, 01),
        LocalDate.of(1992, 03, 01),
        "GoOd");

    assertThat(users.size()).isEqualTo(1);
    assertThat(users.get(0).getName()).isEqualTo(testUser.getName());
  }
}
