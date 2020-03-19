package wolox.training.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import wolox.training.constants.StatusMessages;
import wolox.training.models.User;
import wolox.training.repositories.UserRepository;

import java.util.ArrayList;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private UserRepository userRepository;

  @Override
  public Authentication authenticate(Authentication authentication) {

    String name = authentication.getName();
    String password = authentication.getCredentials().toString();

    User user = userRepository.findFirstByUsername(name).orElseThrow(() -> new BadCredentialsException(StatusMessages.INVALID_CREDENTIALS));
    if (passwordEncoder.matches(password, user.getPassword())) {
      return new UsernamePasswordAuthenticationToken(name, password, new ArrayList<>());
    } else {
      throw new BadCredentialsException(StatusMessages.INVALID_CREDENTIALS);
    }
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return authentication.equals(UsernamePasswordAuthenticationToken.class);
  }
}
