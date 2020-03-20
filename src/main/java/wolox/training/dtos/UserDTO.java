package wolox.training.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public class UserDTO {

  private Long id;

  private String username;

  private String password;

  @JsonProperty("old_password")
  private String oldPassword;

  private String name;

  private LocalDate birthdate;

  /**
   * Instantiates a new User DTO object.
   */
  public UserDTO() {}

  /**
   * User DTO constructor.
   *  @param username  the username
   * @param name      the name
   * @param birthdate the birthdate
   * @param password  the password
   * @param oldPassword the old password
   */
  public UserDTO(String username, String name, LocalDate birthdate, String password, String oldPassword) {
    this.username = username;
    this.name = name;
    this.birthdate = birthdate;
    this.password = password;
    this.oldPassword = oldPassword;
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

  public String getOldPassword() { return oldPassword; }
}
