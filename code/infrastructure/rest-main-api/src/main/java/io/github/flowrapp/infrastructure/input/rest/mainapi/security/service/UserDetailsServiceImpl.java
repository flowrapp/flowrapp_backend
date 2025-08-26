package io.github.flowrapp.infrastructure.input.rest.mainapi.security.service;

import io.github.flowrapp.port.input.UserAuthenticationUseCase;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Implementation of UserDetailsService for Spring Security, providing user authentication based on email and password. This is used by
 * Springs DaoAuthenticationProvider custom provider, with PasswordEncoder to authenticate users.
 */

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService, UserDetailsPasswordService {

  private final UserAuthenticationUseCase userAuthenticationUseCase;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return userAuthenticationUseCase.retrieveUserByMail(username)
        .map(this::mapToUser)
        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
  }

  @Override
  public UserDetails updatePassword(UserDetails user, String newPassword) {
    return userAuthenticationUseCase.updateUserPasswordHash(user.getUsername(), newPassword)
        .map(this::mapToUser)
        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + user.getUsername()));
  }

  private UserDetails mapToUser(io.github.flowrapp.model.User user) {
    return User.withUsername(user.mail())
        .password(user.passwordHash().value())
        .disabled(!user.enabled())
        .authorities(user.role().name())
        .build();
  }

}
