package io.github.flowrapp.infrastructure.input.rest.mainapi.security;

import static java.util.Collections.emptyList;

import java.util.List;

import io.github.flowrapp.port.input.UserAuthenticationUseCase;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Implementation of UserDetailsService for Spring Security, providing user authentication based on email and password.
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl extends AbstractUserDetailsAuthenticationProvider {

  private final UserAuthenticationUseCase userAuthenticationUseCase;

  private final PasswordEncoder passwordEncoder;

  @Override
  protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication)
      throws AuthenticationException {
    if (!passwordEncoder.matches(authentication.getCredentials().toString(), userDetails.getPassword())) {
      throw new BadCredentialsException("Invalid credentials");
    }
  }

  @Override
  protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
    return userAuthenticationUseCase.retrieveUserByMail(username)
        .map(this::mapToUser)
        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
  }

  private UserDetails mapToUser(io.github.flowrapp.model.User user) {
    return User.withUsername(user.name())
        .password(user.passwordHash())
        .disabled(!user.enabled())
        .authorities(user.name().equalsIgnoreCase("admin")
            ? new SimpleGrantedAuthority("ADMIN") : new SimpleGrantedAuthority("USER"))
        .build();
  }

}
