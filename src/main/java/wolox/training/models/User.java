package wolox.training.models;

import com.google.common.base.Preconditions;
import io.swagger.annotations.ApiModel;
import java.time.LocalDate;
import java.util.ArrayList;
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

import wolox.training.constants.StatusMessages;
import wolox.training.exceptions.BookAlreadyOwnedException;

/**
 * The type User.
 */
@Entity
@Table(name = "users")
@ApiModel(description= "Users model")
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

  @ManyToMany(cascade = {CascadeType.REFRESH, CascadeType.MERGE})
  private List<Book> books = new ArrayList<>();

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
   */
  public User(String username, String name, LocalDate birthdate) {
    this.setUsername(username);
    this.setName(name);
    this.setBirthdate(birthdate);
  }

  public Long getId() { return id; }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    Preconditions.checkNotNull(username, StatusMessages.CANNOT_BE_NULL, "username");
    this.username = username;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    Preconditions.checkNotNull(name, StatusMessages.CANNOT_BE_NULL, "name");
    this.name = name;
  }

  public LocalDate getBirthdate() {
    return birthdate;
  }

  public void setBirthdate(LocalDate birthdate) {
    Preconditions.checkNotNull(birthdate, StatusMessages.CANNOT_BE_NULL, "birthdate");
    Preconditions.checkArgument(birthdate.isBefore(LocalDate.now()), StatusMessages.FUTURE_DATE, "birthdate");
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
