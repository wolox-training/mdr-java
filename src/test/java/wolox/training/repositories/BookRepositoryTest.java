package wolox.training.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;
import wolox.training.models.Book;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
class BookRepositoryTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private BookRepository bookRepository;

  Book testBook;

  @BeforeEach
  void setUp() {
    testBook = new Book("Doyle","image","title","subtitle","publisher","1234","500","isbn","terror");
    entityManager.persist(testBook);
    entityManager.flush();
  }

  // findByAuthor

  @Test
  public void whenFindByAuthor_thenReturnBookObject() {
    Optional<Book> found = bookRepository.findFirstByAuthor(testBook.getAuthor());

    assertThat(found.get().getTitle())
        .isEqualTo(testBook.getTitle());
  }

  @Test
  public void whenFindByAuthor_thenReturnNull() {
    Optional<Book> found = bookRepository.findFirstByAuthor(testBook.getAuthor() + "notMatch");

    assertThat(found.isPresent()).isFalse();
  }

  // findById

  @Test
  public void whenFindById_thenReturnBookObject() {
    Optional<Book> found = bookRepository.findById(testBook.getId());

    assertThat(found.get().getTitle())
        .isEqualTo(testBook.getTitle());
  }

  @Test
  public void whenFindById_thenReturnNull() {
    Optional<Book> found = bookRepository.findById(testBook.getId() + 1);

    assertThat(found.isPresent()).isFalse();
  }

  // deleteById

  @Test
  public void whenDeleteById_thenReturnBookObject() {
    Optional<Book> firstFound = bookRepository.findById(testBook.getId());
    assertThat(firstFound.get().getTitle())
        .isEqualTo(testBook.getTitle());

    bookRepository.deleteById(testBook.getId());
    Optional<Book> secondFound = bookRepository.findById(testBook.getId());

    assertThat(secondFound.isPresent()).isFalse();
  }

  // findFirstByIsbn

  @Test
  public void whenFindFirstByIsbn_thenReturnBookObject() {
    Optional<Book> found = bookRepository.findFirstByIsbn(testBook.getIsbn());

    assertThat(found.get().getTitle())
        .isEqualTo(testBook.getTitle());
  }

  @Test
  public void whenFindFirstByIsbn_thenReturnNull() {
    Optional<Book> found = bookRepository.findFirstByIsbn(testBook.getIsbn().concat("not match"));

    assertThat(found.isPresent()).isFalse();
  }

  // findAllByPublisherAndGenreAndYear

  @Test
  public void whenFindAllByPublisherAndGenreAndYear_thenReturnBookObject() {
    Book testBook2 = new Book("Doyle","image","title","subtitle","publisher","1234","500","isbn","terror");
    entityManager.persist(testBook2);
    entityManager.flush();

    List<Book> foundList = bookRepository.findAllByPublisherAndGenreAndYear(testBook.getPublisher(),testBook.getGenre(),testBook.getYear());

    assertThat(foundList.size())
        .isEqualTo(2);
  }

  @Test
  public void whenFindAllByPublisherAndGenreAndYear_thenReturnNull() {
    List<Book> foundList = bookRepository.findAllByPublisherAndGenreAndYear(testBook.getPublisher().concat("not match"),testBook.getGenre(),testBook.getYear());

    assertThat(foundList.size())
        .isEqualTo(0);
  }
}
