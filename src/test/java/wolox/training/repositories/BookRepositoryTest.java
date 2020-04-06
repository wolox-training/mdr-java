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

  Book testBook, testBook2;

  @BeforeEach
  void setUp() {
    testBook = new Book("Doyle","image","title","subtitle","publisher","1234","500","isbn","terror");
    testBook2 = new Book("Doyle","image","title","subtitle","publisher two","1234","500","isbn","terror");
    entityManager.persist(testBook);
    entityManager.persist(testBook2);
    entityManager.flush();
  }

  // findByAuthor

  @Test
  public void whenFindByAuthor_thenReturnBookObject() {
    Optional<Book> found = bookRepository.findFirstByAuthor(testBook.getAuthor());

    assertThat(found.get().getTitle()).isEqualTo(testBook.getTitle());
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

    assertThat(found.get().getTitle()).isEqualTo(testBook.getTitle());
  }

  @Test
  public void whenFindById_thenReturnNull() {
    Optional<Book> found = bookRepository.findById(testBook.getId() + 1000);

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

    assertThat(found.get().getTitle()).isEqualTo(testBook.getTitle());
  }

  @Test
  public void whenFindFirstByIsbn_thenReturnNull() {
    Optional<Book> found = bookRepository.findFirstByIsbn(testBook.getIsbn().concat("not match"));

    assertThat(found.isPresent()).isFalse();
  }

  // findAllByPublisherAndGenreAndYear

  @Test
  public void whenFindAllByPublisherAndGenreAndYear_thenReturnBookObject() {
    List<Book> foundList = bookRepository.findAllByPublisherAndGenreAndYear(testBook.getPublisher(),testBook.getGenre(),testBook.getYear());

    assertThat(foundList.size()).isEqualTo(2);
  }

  @Test
  public void whenFindAllByPublisherAndGenreAndYear_thenReturnNull() {
    List<Book> foundList = bookRepository.findAllByPublisherAndGenreAndYear(testBook.getPublisher().concat("not match"),testBook.getGenre(),testBook.getYear());

    assertThat(foundList.size()).isEqualTo(0);
  }

  @Test
  public void whenFindAllByPublisherAndGenreAndYearWithSomeNullParameters_thenReturnBookObject() {
    List<Book> foundList = bookRepository.findAllByPublisherAndGenreAndYear(null,testBook.getGenre(),testBook.getYear());

    assertThat(foundList.size()).isEqualTo(2);
    assertThat(foundList.get(0).getTitle()).isEqualTo(testBook.getTitle());
    assertThat(foundList.get(1).getTitle()).isEqualTo(testBook2.getTitle());
  }

  @Test
  public void whenFindAllByPublisherAndGenreAndYearWithAllNullParameters_thenReturnBookObject() {
    List<Book> foundList = bookRepository.findAllByPublisherAndGenreAndYear(null,null,null);

    assertThat(foundList.size())
        .isEqualTo(2);
    assertThat(foundList.get(0).getTitle()).isEqualTo(testBook.getTitle());
    assertThat(foundList.get(1).getTitle()).isEqualTo(testBook2.getTitle());
  }

  // findAllWithFilters

  @Test
  public void whenFindAllWithFilters_thenReturnBookObject() {
    List<Book> foundList = bookRepository.findAllWithFilters(
        testBook.getGenre(),
        testBook.getAuthor(),
        testBook.getImage(),
        testBook.getTitle(),
        testBook.getSubtitle(),
        testBook.getPublisher(),
        testBook.getYear(),
        testBook.getPages(),
        testBook.getIsbn()
    );

    assertThat(foundList.size()).isEqualTo(1);
  }

  @Test
  public void whenFindAllWithFilters_thenReturnNull() {
    List<Book> foundList = bookRepository.findAllWithFilters(
        testBook.getGenre().concat("not matching string"),
        testBook.getAuthor(),
        testBook.getImage(),
        testBook.getTitle(),
        testBook.getSubtitle(),
        testBook.getPublisher(),
        testBook.getYear(),
        testBook.getPages(),
        testBook.getIsbn()
    );

    assertThat(foundList.size()).isEqualTo(0);
  }

  @Test
  public void whenFindAllWithFiltersWithSomeNullParameters_thenReturnBookObject() {
    Book testBook3 = new Book(
        "Doyle",
        "image",
        "title",
        "subtitle",
        "publisher three",
        "1234",
        "500",
        "isbn",
        "terror"
    );
    entityManager.persist(testBook3);
    entityManager.flush();

    List<Book> foundList = bookRepository.findAllWithFilters(
        testBook.getGenre(),
        testBook.getAuthor(),
        testBook.getImage(),
        testBook.getTitle(),
        testBook.getSubtitle(),
        "",
        "",
        "",
        ""
    );

    assertThat(foundList.size()).isEqualTo(3);
    assertThat(foundList.get(0).getTitle()).isEqualTo(testBook.getTitle());
    assertThat(foundList.get(1).getTitle()).isEqualTo(testBook2.getTitle());
    assertThat(foundList.get(2).getTitle()).isEqualTo(testBook3.getTitle());
  }

  @Test
  public void whenFindAllWithFiltersWithAllNullParameters_thenReturnBookObject() {
    List<Book> foundList = bookRepository.findAllWithFilters("","","","","","","","","");

    assertThat(foundList.size()).isEqualTo(2);
    assertThat(foundList.get(0).getTitle()).isEqualTo(testBook.getTitle());
    assertThat(foundList.get(1).getTitle()).isEqualTo(testBook2.getTitle());
  }
}
