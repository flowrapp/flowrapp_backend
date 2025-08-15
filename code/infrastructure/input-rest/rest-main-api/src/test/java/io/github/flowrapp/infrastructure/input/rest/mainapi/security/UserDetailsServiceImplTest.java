package io.github.flowrapp.infrastructure.input.rest.mainapi.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import io.github.flowrapp.port.input.UserAuthenticationUseCase;

import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class UserDetailsServiceImplTest {

  @Mock
  private UserAuthenticationUseCase userAuthenticationUseCase;

  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private UserDetailsServiceImpl userDetailsService;

  @ParameterizedTest
  @InstancioSource
  void additionalAuthenticationChecks(User userDetails, UsernamePasswordAuthenticationToken authentication) {
    // GIVEN
    when(passwordEncoder.matches(authentication.getCredentials().toString(), userDetails.getPassword()))
        .thenReturn(true);

    // This method is tested indirectly through the authentication process
    // You can add specific tests for password matching if needed
    assertDoesNotThrow(() -> userDetailsService.additionalAuthenticationChecks(userDetails, authentication));
  }

  @ParameterizedTest
  @InstancioSource
  void additionalAuthenticationChecksThrows(User userDetails, UsernamePasswordAuthenticationToken authentication) {
    // GIVEN
    when(passwordEncoder.matches(authentication.getCredentials().toString(), userDetails.getPassword()))
        .thenReturn(false);

    // WHEN & THEN
    assertThrows(BadCredentialsException.class,
        () -> userDetailsService.additionalAuthenticationChecks(userDetails, authentication));
  }

  @ParameterizedTest
  @InstancioSource
  void retrieveUser(String username, io.github.flowrapp.model.User user, UsernamePasswordAuthenticationToken authentication) {
    // GIVEN
    when(userAuthenticationUseCase.retrieveUserByMail(username))
        .thenReturn(Optional.of(user));

    // WHEN
    User retrievedUser = (User) userDetailsService.retrieveUser(username, authentication);

    // THEN
    assertThat(retrievedUser)
        .isNotNull()
        .extracting(User::getUsername, User::getPassword)
        .containsExactly(user.mail(), user.passwordHash());
  }

  @ParameterizedTest
  @InstancioSource
  void retrieveUserAdmin(String username, io.github.flowrapp.model.User user, UsernamePasswordAuthenticationToken authentication) {
    // GIVEN
    user = user.toBuilder().name("admin").build(); // Ensure the user is an admin
    when(userAuthenticationUseCase.retrieveUserByMail(username))
        .thenReturn(Optional.of(user));

    // WHEN
    User retrievedUser = (User) userDetailsService.retrieveUser(username, authentication);

    // THEN
    assertNotNull(retrievedUser);
    assertThat(retrievedUser.getAuthorities())
        .isNotEmpty()
        .extracting(GrantedAuthority::getAuthority)
        .containsExactly("ADMIN");
  }

  @ParameterizedTest
  @InstancioSource
  void retrieveUserNotFound(String username, UsernamePasswordAuthenticationToken authentication) {
    // GIVEN
    when(userAuthenticationUseCase.retrieveUserByMail(username))
        .thenReturn(Optional.empty());

    // WHEN & THEN
    assertThrows(UsernameNotFoundException.class,
        () -> userDetailsService.retrieveUser(username, authentication));
  }

}
