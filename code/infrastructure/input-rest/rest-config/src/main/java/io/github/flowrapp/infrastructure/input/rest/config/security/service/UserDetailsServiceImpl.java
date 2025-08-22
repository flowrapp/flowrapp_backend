package io.github.flowrapp.infrastructure.input.rest.config.security.service;

import io.github.flowrapp.port.input.UserAuthenticationUseCase;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Implementation of UserDetailsService for Spring Security, providing user authentication based on email and password. This is used by
 * Springs DaoAuthenticationProvider custom provider, with PasswordEncoder to auhenticate users.
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

  private final UserAuthenticationUseCase userAuthenticationUseCase;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return userAuthenticationUseCase.retrieveUserByMail(username)
        .map(this::mapToUser)
        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
  }

  private UserDetails mapToUser(io.github.flowrapp.model.User user) {
    return User.withUsername(user.mail())
        .password(user.passwordHash().get())
        .disabled(!user.enabled())
        .authorities(user.name().equalsIgnoreCase("admin")
            ? new SimpleGrantedAuthority("SCOPE_ADMIN")
            : new SimpleGrantedAuthority("SCOPE_USER"))
        .build();
  }

}
