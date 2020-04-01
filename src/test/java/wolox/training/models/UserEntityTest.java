package wolox.training.models;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;
import wolox.training.constants.StatusMessages;
import wolox.training.repositories.UserRepository;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
class UserEntityTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private UserRepository userRepository;

  // write test cases here

  @Test
  public void whenCreateUserWithNullArgument_thenThrowError() {
    try {
      new User("username", null, LocalDate.of(3000, 02, 02), "encodedPassword");
    } catch (Exception e) {
      assertThat(e.getMessage().equals(String.format(StatusMessages.CANNOT_BE_NULL, "name")));
    }
  }

  @Test
  public void whenCreateUserWithBlankField_thenThrowError() {
    try {
      new User("", "test name", LocalDate.of(1992, 02, 02), "encodedPassword");
    } catch (Exception e) {
      assertThat(e.getMessage().equals(String.format(StatusMessages.CANNOT_BE_EMPTY, "username")));
    }
  }

  @Test
  public void whenCreateUserWithWrongBirthdate_thenThrowError() {
    try {
      new User("username", "test name", LocalDate.of(3000, 02, 02), "encodedPassword");
    } catch (Exception e) {
      assertThat(e.getMessage().equals(String.format(StatusMessages.FUTURE_DATE, "birthdate")));
    }
  }
}
