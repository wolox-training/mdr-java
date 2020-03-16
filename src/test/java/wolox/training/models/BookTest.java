package wolox.training.models;

    import org.junit.jupiter.api.Test;
    import org.junit.runner.RunWith;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
    import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
    import org.springframework.test.context.junit4.SpringRunner;
    import wolox.training.constants.StatusMessages;
    import wolox.training.repositories.BookRepository;

    import java.util.Optional;

    import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
class BookIntegrationTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private BookRepository bookRepository;

  // write test cases here

  @Test
  public void whenFindByAuthor_thenReturnBookObject() {
    Book testBook = new Book("Doyle","image","title","subtitle","publisher","1234","500","isbn","terror");
    entityManager.persist(testBook);
    entityManager.flush();

    Optional<Book> found = bookRepository.findFirstByAuthor(testBook.getAuthor());

    assertThat(found.get().getTitle())
        .isEqualTo(testBook.getTitle());
  }

  @Test
  public void whenFindByUsername_thenReturnNull() {
    Book testBook = new Book("Doyle","image","title","subtitle","publisher","1234","500","isbn","terror");
    entityManager.persist(testBook);
    entityManager.flush();

    Optional<Book> found = bookRepository.findFirstByAuthor(testBook.getAuthor() + "notMatch");

    assertThat(!found.isPresent());
  }

  @Test
  public void whenCreateBookWithNullArgument_thenThrowError() {
    try {
      new Book("Doyle",null,"title","subtitle","publisher","1234","500","isbn","terror");
    } catch (Exception e) {
      assertThat(e.getMessage().equals(String.format(StatusMessages.CANNOT_BE_NULL, "image")));
    }
  }

  @Test
  public void whenCreateBookWithBlankField_thenThrowError() {
    try {
      new Book("","image","title","subtitle","publisher","1234","500","isbn","terror");
    } catch (Exception e) {
      assertThat(e.getMessage().equals(String.format(StatusMessages.CANNOT_BE_EMPTY, "author")));
    }
  }

  @Test
  public void whenCreateBookWithWrongPages_thenThrowError() {
    try {
      new Book("Doyle","image","title","subtitle","publisher","1234","0","isbn","terror");
    } catch (Exception e) {
      assertThat(e.getMessage().equals(String.format(StatusMessages.PAGES_QUANTITY)));
    }
  }
}
