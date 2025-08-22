package io.github.flowrapp.infrastructure.output.adapters.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import io.github.flowrapp.infrastructure.input.rest.config.security.value.ClaimConstants;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.jwt.Jwt;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class UserSecurityContextHolderAdapterTest {

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private MockedStatic<SecurityContextHolder> mockedStatic;

  @Mock
  private UserRepositoryAdapter userRepositoryAdapter;

  @InjectMocks
  private UserSecurityContextHolderAdapter userSecurityContextHolderAdapter;

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void getCurrentUserEmail(User user) {
    // GIVEN
    this.mockedStatic.when(() -> SecurityContextHolder.getContext().getAuthentication().getPrincipal())
        .thenReturn(user);

    // WHEN
    var result = this.userSecurityContextHolderAdapter.getCurrentUserEmail();

    // THEN
    assertThat(result)
        .isPresent()
        .hasValue(user.getUsername());
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void getCurrentUserEmailJwt(String mail) {
    // GIVEN
    var jwt = mock(Jwt.class);
    when(jwt.getClaimAsString(ClaimConstants.CLAIM_KEY_USER_MAIL)).thenReturn(mail);
    this.mockedStatic.when(() -> SecurityContextHolder.getContext().getAuthentication().getPrincipal())
        .thenReturn(jwt);

    // WHEN
    var result = this.userSecurityContextHolderAdapter.getCurrentUserEmail();

    // THEN
    assertThat(result)
        .isPresent()
        .hasValue(mail);
  }

  @Test
  void getCurrentUserEmailJwt() {
    // GIVEN
    this.mockedStatic.when(() -> SecurityContextHolder.getContext().getAuthentication().getPrincipal())
        .thenReturn(new Object());

    // WHEN
    var result = this.userSecurityContextHolderAdapter.getCurrentUserEmail();

    // THEN
    assertThat(result)
        .isNotPresent();
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void getCurrentUser(User user, io.github.flowrapp.model.User userModel) {
    // GIVEN
    this.mockedStatic.when(() -> SecurityContextHolder.getContext().getAuthentication().getPrincipal())
        .thenReturn(user);
    when(userRepositoryAdapter.findUserByEmail(user.getUsername()))
        .thenReturn(Optional.of(userModel));

    // WHEN
    var result = this.userSecurityContextHolderAdapter.getCurrentUser();

    // THEN
    assertThat(result)
        .isEqualTo(userModel);
  }

}
