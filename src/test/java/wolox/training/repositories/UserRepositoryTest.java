package wolox.training.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;
import wolox.training.models.User;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
class UserRepositoryTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private UserRepository userRepository;

  User testUser, testUser2, testUser3;
  Pageable pageable;

  @BeforeEach
  void setUp() {
    testUser = new User("username","good name", LocalDate.of(1992, 02, 05), "encodedPassword");
    testUser2 = new User("username2","good name 2", LocalDate.of(1992, 05, 01), "encodedPassword");
    testUser3 = new User("username3","bad name", LocalDate.of(1992, 02, 15), "encodedPassword");
    entityManager.persist(testUser);
    entityManager.persist(testUser2);
    entityManager.persist(testUser3);
    entityManager.flush();
  }

  // findByUsername

  @Test
  public void whenFindByUsername_thenReturnUserObject() {
    Optional<User> found = userRepository.findFirstByUsername(testUser.getUsername());

    assertThat(found.get().getUsername())
        .isEqualTo(testUser.getUsername());
  }

  @Test
  public void whenFindByUsername_thenReturnNull() {
    Optional<User> found = userRepository.findFirstByUsername(testUser.getUsername() + "notMatch");

    assertThat(found.isPresent()).isFalse();
  }

  // findAllByBirthdateAndName

  @Test
  public void whenFindAllByBirthdateAndName_thenReturnUsers() {
    Page<User> page = userRepository.findAllByBirthdateAndName(
        LocalDate.of(1992, 02, 01),
        LocalDate.of(1992, 03, 01),
        "GoOd",
        pageable
    );

    assertThat(page.getTotalElements()).isEqualTo(1);
    assertThat(page.getContent().get(0).getName()).isEqualTo(testUser.getName());
  }

  @Test
  public void whenFindAllByBirthdateAndNameWithNullParameter_thenReturnUsers() {
    Page<User> page = userRepository.findAllByBirthdateAndName(
        null,
        null,
        "GoOd",
        pageable
    );

    assertThat(page.getTotalElements()).isEqualTo(2);
    assertThat(page.getContent().get(0).getName()).isEqualTo(testUser.getName());
    assertThat(page.getContent().get(1).getName()).isEqualTo(testUser2.getName());
  }

  @Test
  public void whenFindAllByBirthdateAndNameWithAllNullParameter_thenReturnUsers() {
    Page<User> page = userRepository.findAllByBirthdateAndName(null,null,null,pageable);

    assertThat(page.getTotalElements()).isEqualTo(3);
    assertThat(page.getContent().get(0).getName()).isEqualTo(testUser.getName());
    assertThat(page.getContent().get(1).getName()).isEqualTo(testUser2.getName());
    assertThat(page.getContent().get(2).getName()).isEqualTo(testUser3.getName());
  }

  // findAllWithFilters

  @Test
  public void whenFindAllWithFilters_thenReturnUsers() {
    Page<User> page = userRepository.findAllWithFilters(
        testUser.getBirthdate(),
        testUser.getName(),
        testUser.getUsername(),
        pageable
    );

    assertThat(page.getTotalElements()).isEqualTo(1);
    assertThat(page.getContent().get(0).getName()).isEqualTo(testUser.getName());
  }

  @Test
  public void whenFindAllWithFiltersWithNullParameter_thenReturnUsers() {
    Page<User> page = userRepository.findAllWithFilters(null,"gooD","",pageable);

    assertThat(page.getTotalElements()).isEqualTo(2);
    assertThat(page.getContent().get(0).getName()).isEqualTo(testUser.getName());
    assertThat(page.getContent().get(1).getName()).isEqualTo(testUser2.getName());
  }

  @Test
  public void whenFindAllWithFiltersWithAllNullParameter_thenReturnUsers() {
    Page<User> page = userRepository.findAllWithFilters(null,"","",pageable);

    assertThat(page.getTotalElements()).isEqualTo(3);
    assertThat(page.getContent().get(0).getName()).isEqualTo(testUser.getName());
    assertThat(page.getContent().get(1).getName()).isEqualTo(testUser2.getName());
    assertThat(page.getContent().get(2).getName()).isEqualTo(testUser3.getName());
  }
}
