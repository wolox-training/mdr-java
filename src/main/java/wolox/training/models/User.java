package wolox.training.models;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import wolox.training.exceptions.BookAlreadyOwnedException;

/**
 * The type User.
 */
@Entity
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;

  @Column(nullable = false)
  private String username;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private LocalDate birthdate;

  @ManyToMany(cascade = CascadeType.ALL)
  private List<Book> books;

  /**
   * Instantiates a new User.
   */
  public User() {}

  /**
   * User constructor.
   *
   * @param username  the username
   * @param name      the name
   * @param birthdate the birthdate
   * @param books     the books
   */
  public User(String username, String name, LocalDate birthdate, List<Book> books) {
    this.username = username;
    this.name = name;
    this.birthdate = birthdate;
    this.books = books;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public LocalDate getBirthdate() {
    return birthdate;
  }

  public void setBirthdate(LocalDate birthdate) {
    this.birthdate = birthdate;
  }

  public List<Book> getBooks() {
    return Collections.unmodifiableList(books);
  }

  public void addBook(Book book) {
    if(this.books.contains(book)) throw new BookAlreadyOwnedException();
    this.books.add(book);
  }

  public void removeBook(Book book) {
    this.books.remove(book);
  }
}
