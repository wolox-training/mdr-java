package wolox.training.models;

import com.google.common.base.Preconditions;
import wolox.training.constants.StatusMessages;

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

  @Column(nullable = false)
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
    this.genre = genre;
    this.author = author;
    this.image = image;
    this.title = title;
    this.subtitle = subtitle;
    this.publisher = publisher;
    this.year = year;
    this.pages = pages;
    this.isbn = isbn;
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
    this.author = Preconditions.checkNotNull(author, StatusMessages.CANNOT_BE_NULL, "author");
  }

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = Preconditions.checkNotNull(image, StatusMessages.CANNOT_BE_NULL, "image");
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = Preconditions.checkNotNull(title, StatusMessages.CANNOT_BE_NULL, "title");
  }

  public String getSubtitle() {
    return subtitle;
  }

  public void setSubtitle(String subtitle) {
    this.subtitle = Preconditions.checkNotNull(subtitle, StatusMessages.CANNOT_BE_NULL, "subtitle");
  }

  public String getPublisher() {
    return publisher;
  }

  public void setPublisher(String publisher) {
    this.publisher = Preconditions.checkNotNull(publisher, StatusMessages.CANNOT_BE_NULL, "publisher");
  }

  public String getYear() {
    return year;
  }

  public void setYear(String year) {
    this.year = Preconditions.checkNotNull(year, StatusMessages.CANNOT_BE_NULL, "year");
  }

  public String getPages() {
    return pages;
  }

  public void setPages(String pages) {
    this.pages = Preconditions.checkNotNull(pages, StatusMessages.CANNOT_BE_NULL, "pages");
  }

  public String getIsbn() {
    return isbn;
  }

  public void setIsbn(String isbn) {
    this.isbn = Preconditions.checkNotNull(isbn, StatusMessages.CANNOT_BE_NULL, "isbn");
  }

  public Long getId() { return id; }

}
