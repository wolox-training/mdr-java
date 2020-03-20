package wolox.training.models;

import com.google.common.base.Preconditions;
import wolox.training.constants.StatusMessages;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

/**
 * The type Book.
 */
@Entity
public class Book {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;

  @Column
  private String genre;

  @Column(nullable = false)
  private String author;

  @Column
  private String image;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private String subtitle;

  @Column(nullable = false)
  private String publisher;

  @Column(nullable = false)
  private String year;

  @Column(nullable = false)
  private String pages;

  @Column(nullable = false)
  private String isbn;

  @ManyToMany(mappedBy = "books")
  private List<User> users = new ArrayList<>();

  /**
   * Instantiates a new Book.
   */
  public Book() {};

  /**
   * Book constructor
   *
   * @param author    the author
   * @param image     the image
   * @param title     the title
   * @param subtitle  the subtitle
   * @param publisher the publisher
   * @param year      the year
   * @param pages     the pages
   * @param isbn      the isbn
   * @param genre     the genre
   */
  public Book(String author, String image, String title, String subtitle, String publisher, String year, String pages, String isbn, String genre) {
    this.setGenre(genre);
    this.setAuthor(author);
    this.setImage(image);
    this.setTitle(title);
    this.setSubtitle(subtitle);
    this.setPublisher(publisher);
    this.setYear(year);
    this.setPages(pages);
    this.setIsbn(isbn);
  }

  public String getGenre() {
    return genre;
  }

  public void setGenre(String genre) {
    this.genre = genre;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    Preconditions.checkNotNull(author, StatusMessages.CANNOT_BE_NULL, "author");
    Preconditions.checkArgument(!author.isEmpty(), StatusMessages.CANNOT_BE_EMPTY, "author");
    this.author = author;
  }

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    Preconditions.checkNotNull(title, StatusMessages.CANNOT_BE_NULL, "title");
    Preconditions.checkArgument(!title.isEmpty(), StatusMessages.CANNOT_BE_EMPTY, "title");
    this.title = title;
  }

  public String getSubtitle() {
    return subtitle;
  }

  public void setSubtitle(String subtitle) {
    Preconditions.checkNotNull(subtitle, StatusMessages.CANNOT_BE_NULL, "subtitle");
    Preconditions.checkArgument(!subtitle.isEmpty(), StatusMessages.CANNOT_BE_EMPTY, "subtitle");
    this.subtitle = subtitle;
  }

  public String getPublisher() {
    return publisher;
  }

  public void setPublisher(String publisher) {
    Preconditions.checkNotNull(publisher, StatusMessages.CANNOT_BE_NULL, "publisher");
    Preconditions.checkArgument(!publisher.isEmpty(), StatusMessages.CANNOT_BE_EMPTY, "publisher");
    this.publisher = publisher;
  }

  public String getYear() {
    return year;
  }

  public void setYear(String year) {
    Preconditions.checkNotNull(year, StatusMessages.CANNOT_BE_NULL, "year");
    Preconditions.checkArgument(Integer.parseInt(year) > 0 && Integer.parseInt(year) <= LocalDate.now().getYear(), StatusMessages.INVALID_YEAR);
    this.year = year;
  }

  public String getPages() {
    return pages;
  }

  public void setPages(String pages) {
    Preconditions.checkNotNull(pages, StatusMessages.CANNOT_BE_NULL, "pages");
    Preconditions.checkArgument(Integer.parseInt(pages) > 0, StatusMessages.PAGES_QUANTITY, "pages");
    this.pages = pages;
  }

  public String getIsbn() {
    return isbn;
  }

  public void setIsbn(String isbn) {
    Preconditions.checkNotNull(isbn, StatusMessages.CANNOT_BE_NULL, "isbn");
    Preconditions.checkArgument(!isbn.isEmpty(), StatusMessages.CANNOT_BE_EMPTY, "isbn");
    this.isbn = isbn;
  }

  public Long getId() { return id; }

}
