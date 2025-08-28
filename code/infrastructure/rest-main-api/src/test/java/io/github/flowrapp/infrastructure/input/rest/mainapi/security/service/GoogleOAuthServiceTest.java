package io.github.flowrapp.infrastructure.input.rest.mainapi.security.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.github.flowrapp.value.OAuth2UserInfo;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class GoogleOAuthServiceTest {

  @Mock
  private GoogleIdTokenVerifier verifier;

  @InjectMocks
  private GoogleOAuthService googleOAuthService;

  @ParameterizedTest
  @InstancioSource(samples = 5)
  void validateTokenAndGetUser_SuccessfulValidation_CompleteUserData(String idToken, Payload payload, String name) throws Exception {
    // GIVEN
    payload.setEmail("mail@mail.com");
    payload.set("name", name);
    var token = mock(GoogleIdToken.class);
    when(token.getPayload()).thenReturn(payload);
    when(verifier.verify(idToken)).thenReturn(token);

    // WHEN
    var result = googleOAuthService.validateTokenAndGetUser(idToken);

    assertThat(result)
        .isPresent();
  }

  @ParameterizedTest
  @InstancioSource(samples = 5)
  void validateTokenAndGetUser_SuccessfulValidation_WithMailFallback(String idToken, Payload payload) throws Exception {
    // GIVEN
    payload.setEmail("mail@mail.com");
    var token = mock(GoogleIdToken.class);
    when(token.getPayload()).thenReturn(payload);
    when(verifier.verify(idToken)).thenReturn(token);

    // WHEN
    var result = googleOAuthService.validateTokenAndGetUser(idToken);

    assertThat(result)
        .isPresent()
        .get()
        .returns("mail", OAuth2UserInfo::getName);
  }

  @ParameterizedTest
  @InstancioSource(samples = 5)
  void validateTokenAndGetUser_isEmpty(String idToken) throws Exception {
    // GIVEN

    // WHEN
    var result = googleOAuthService.validateTokenAndGetUser(idToken);

    assertThat(result)
        .isEmpty();
  }

}
