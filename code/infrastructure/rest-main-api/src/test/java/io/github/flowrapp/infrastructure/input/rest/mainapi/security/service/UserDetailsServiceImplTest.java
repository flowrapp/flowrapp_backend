package io.github.flowrapp.infrastructure.input.rest.mainapi.security.service;

import static org.assertj.core.api.Assertions.assertThat;
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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class UserDetailsServiceImplTest {

  @Mock
  private UserAuthenticationUseCase userAuthenticationUseCase;

  @InjectMocks
  private UserDetailsServiceImpl userDetailsService;

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void retrieveUser(String username, io.github.flowrapp.model.User user) {
    // GIVEN
    when(userAuthenticationUseCase.retrieveUserByMail(username))
        .thenReturn(Optional.of(user));

    // WHEN
    User retrievedUser = (User) userDetailsService.loadUserByUsername(username);

    // THEN
    assertThat(retrievedUser)
        .isNotNull()
        .extracting(User::getUsername, User::getPassword)
        .containsExactly(user.mail(), user.passwordHash().get());
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void retrieveUserAdmin(String username, io.github.flowrapp.model.User user) {
    // GIVEN
    user = user.toBuilder().name("admin").build(); // Ensure the user is an admin
    when(userAuthenticationUseCase.retrieveUserByMail(username))
        .thenReturn(Optional.of(user));

    // WHEN
    User retrievedUser = (User) userDetailsService.loadUserByUsername(username);

    // THEN
    assertNotNull(retrievedUser);
    assertThat(retrievedUser.getAuthorities())
        .isNotEmpty()
        .extracting(GrantedAuthority::getAuthority)
        .containsExactly(user.role().name());
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void retrieveUserNotFound(String username) {
    // GIVEN
    when(userAuthenticationUseCase.retrieveUserByMail(username))
        .thenReturn(Optional.empty());

    // WHEN & THEN
    assertThrows(UsernameNotFoundException.class,
        () -> userDetailsService.loadUserByUsername(username));
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void updatePassword(User details, String password, io.github.flowrapp.model.User user) {
    // GIVEN
    when(userAuthenticationUseCase.updateUserPasswordHash(details.getUsername(), password))
        .thenReturn(Optional.of(user));

    // WHEN
    var updatedUser = userDetailsService.updatePassword(details, password);

    // THEN
    assertThat(updatedUser)
        .isNotNull()
        .returns(user.passwordHash().get(), UserDetails::getPassword);
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void updatePasswordThrowsException(User details, String password) {
    // GIVEN
    when(userAuthenticationUseCase.updateUserPasswordHash(details.getUsername(), password))
        .thenReturn(Optional.empty());

    // WHEN & THEN
    assertThrows(UsernameNotFoundException.class, () -> userDetailsService.updatePassword(details, password));
  }

}
