package wolox.training.dtos;

import java.time.LocalDate;

public class UserDto {

  private Long id;

  private String username;

  private String password;

  private String name;

  private LocalDate birthdate;

  /**
   * Instantiates a new User DTO object.
   */
  public UserDto() {}

  /**
   * User DTO constructor.
   *
   * @param username  the username
   * @param password  the password
   * @param name      the name
   * @param birthdate the birthdate
   */
  public UserDto(String username, String name, LocalDate birthdate, String password) {
    this.username = username;
    this.name = name;
    this.birthdate = birthdate;
    this.password = password;
  }

  public Long getId() { return id; }

  public String getUsername() {
    return username;
  }

  public String getName() {
    return name;
  }

  public LocalDate getBirthdate() {
    return birthdate;
  }

  public String getPassword() {
    return password;
  }

}
