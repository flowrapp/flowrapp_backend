package io.github.flowrapp.infrastructure.input.rest.mainapi.security.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

import io.github.flowrapp.infrastructure.input.rest.mainapi.security.value.GoogleUserInfo;
import io.github.flowrapp.value.OAuth2UserInfo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class GoogleOAuthServiceTest {

  private static final String TEST_CLIENT_ID = "test-client-id.apps.googleusercontent.com";

  private static final String VALID_ISSUER = "https://accounts.google.com";

  @Mock
  private HttpClient httpClient;

  @Mock
  private ObjectMapper objectMapper;

  private GoogleOAuthService googleOAuthService;

  @BeforeEach
  void setUp() {
    googleOAuthService = new GoogleOAuthService(TEST_CLIENT_ID);
    // Inject mocked dependencies using reflection
    ReflectionTestUtils.setField(googleOAuthService, "httpClient", httpClient);
    ReflectionTestUtils.setField(googleOAuthService, "objectMapper", objectMapper);
  }

  @ParameterizedTest
  @InstancioSource(samples = 5)
  void validateTokenAndGetUser_SuccessfulValidation_CompleteUserData(String idToken, String userId, String email, String name,
      String pictureUrl) throws Exception {
    // GIVEN
    HttpResponse<String> mockResponse = createMockHttpResponse(200, "valid-json");
    when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenReturn(mockResponse);

    ObjectNode jsonNode = createValidTokenInfoResponse(userId, email, name, pictureUrl);
    when(objectMapper.readTree("valid-json")).thenReturn(jsonNode);

    // WHEN
    Optional<OAuth2UserInfo> result = googleOAuthService.validateTokenAndGetUser(idToken);

    // THEN
    assertThat(result).isPresent();
    OAuth2UserInfo userInfo = result.get();
    assertThat(userInfo).isInstanceOf(GoogleUserInfo.class);
    assertThat(userInfo.getId()).isEqualTo(userId);
    assertThat(userInfo.getEmail()).isEqualTo(email);
    assertThat(userInfo.getName()).isEqualTo(name);
    assertThat(userInfo.getAvatarUrl()).isEqualTo(pictureUrl);
    assertThat(userInfo.getProvider()).isEqualTo(OAuth2UserInfo.Provider.GOOGLE);
  }

  @ParameterizedTest
  @InstancioSource(samples = 5)
  void validateTokenAndGetUser_SuccessfulValidation_FallbackToGivenAndFamilyName(String idToken, String userId, String email,
      String givenName, String familyName, String pictureUrl) throws Exception {
    // GIVEN
    HttpResponse<String> mockResponse = createMockHttpResponse(200, "valid-json");
    when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenReturn(mockResponse);

    ObjectNode jsonNode = createTokenInfoResponseWithoutName(userId, email, givenName, familyName, pictureUrl);
    when(objectMapper.readTree("valid-json")).thenReturn(jsonNode);

    // WHEN
    Optional<OAuth2UserInfo> result = googleOAuthService.validateTokenAndGetUser(idToken);

    // THEN
    assertThat(result).isPresent();
    OAuth2UserInfo userInfo = result.get();
    assertThat(userInfo.getName()).isEqualTo(givenName + " " + familyName);
  }

  @ParameterizedTest
  @InstancioSource(samples = 5)
  void validateTokenAndGetUser_SuccessfulValidation_FallbackToGivenNameOnly(String idToken, String userId, String email, String givenName,
      String pictureUrl) throws Exception {
    // GIVEN
    HttpResponse<String> mockResponse = createMockHttpResponse(200, "valid-json");
    when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenReturn(mockResponse);

    ObjectNode jsonNode = createTokenInfoResponseWithGivenNameOnly(userId, email, givenName, pictureUrl);
    when(objectMapper.readTree("valid-json")).thenReturn(jsonNode);

    // WHEN
    Optional<OAuth2UserInfo> result = googleOAuthService.validateTokenAndGetUser(idToken);

    // THEN
    assertThat(result).isPresent();
    OAuth2UserInfo userInfo = result.get();
    assertThat(userInfo.getName()).isEqualTo(givenName);
  }

  @ParameterizedTest
  @InstancioSource(samples = 5)
  void validateTokenAndGetUser_SuccessfulValidation_FallbackToDefaultName(String idToken, String userId, String email, String pictureUrl)
      throws Exception {
    // GIVEN
    HttpResponse<String> mockResponse = createMockHttpResponse(200, "valid-json");
    when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenReturn(mockResponse);

    ObjectNode jsonNode = createTokenInfoResponseWithoutAnyName(userId, email, pictureUrl);
    when(objectMapper.readTree("valid-json")).thenReturn(jsonNode);

    // WHEN
    Optional<OAuth2UserInfo> result = googleOAuthService.validateTokenAndGetUser(idToken);

    // THEN
    assertThat(result).isPresent();
    OAuth2UserInfo userInfo = result.get();
    assertThat(userInfo.getName()).isEqualTo("Google User");
  }

  @ParameterizedTest
  @InstancioSource(samples = 5)
  void validateTokenAndGetUser_InvalidAudience_ReturnsEmpty(String idToken, String userId, String email, String name, String pictureUrl)
      throws Exception {
    // GIVEN
    HttpResponse<String> mockResponse = createMockHttpResponse(200, "valid-json");
    when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenReturn(mockResponse);

    ObjectNode jsonNode = createValidTokenInfoResponse(userId, email, name, pictureUrl);
    jsonNode.put("aud", "wrong-client-id");
    when(objectMapper.readTree("valid-json")).thenReturn(jsonNode);

    // WHEN
    Optional<OAuth2UserInfo> result = googleOAuthService.validateTokenAndGetUser(idToken);

    // THEN
    assertThat(result).isEmpty();
  }

  @ParameterizedTest
  @InstancioSource(samples = 5)
  void validateTokenAndGetUser_InvalidIssuer_ReturnsEmpty(String idToken, String userId, String email, String name, String pictureUrl)
      throws Exception {
    // GIVEN
    HttpResponse<String> mockResponse = createMockHttpResponse(200, "valid-json");
    when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenReturn(mockResponse);

    ObjectNode jsonNode = createValidTokenInfoResponse(userId, email, name, pictureUrl);
    jsonNode.put("iss", "https://malicious-issuer.com");
    when(objectMapper.readTree("valid-json")).thenReturn(jsonNode);

    // WHEN
    Optional<OAuth2UserInfo> result = googleOAuthService.validateTokenAndGetUser(idToken);

    // THEN
    assertThat(result).isEmpty();
  }

  @Test
  void validateTokenAndGetUser_AlternativeValidIssuer_Success() throws Exception {
    // GIVEN
    String idToken = "valid-token";
    HttpResponse<String> mockResponse = createMockHttpResponse(200, "valid-json");
    when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenReturn(mockResponse);

    ObjectNode jsonNode = createValidTokenInfoResponse("user123", "test@gmail.com", "Test User", "http://avatar.url");
    jsonNode.put("iss", "accounts.google.com"); // Alternative valid issuer
    when(objectMapper.readTree("valid-json")).thenReturn(jsonNode);

    // WHEN
    Optional<OAuth2UserInfo> result = googleOAuthService.validateTokenAndGetUser(idToken);

    // THEN
    assertThat(result).isPresent();
  }

  @ParameterizedTest
  @InstancioSource(samples = 3)
  void validateTokenAndGetUser_HttpErrorStatus_ReturnsEmpty(String idToken, int errorStatus) throws Exception {
    // GIVEN
    HttpResponse<String> mockResponse = createMockHttpResponse(errorStatus, "error-response");
    when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenReturn(mockResponse);

    // WHEN
    Optional<OAuth2UserInfo> result = googleOAuthService.validateTokenAndGetUser(idToken);

    // THEN
    assertThat(result).isEmpty();
  }

  @ParameterizedTest
  @InstancioSource(samples = 5)
  void validateTokenAndGetUser_IOException_ReturnsEmpty(String idToken) throws Exception {
    // GIVEN
    when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenThrow(new IOException("Network error"));

    // WHEN
    Optional<OAuth2UserInfo> result = googleOAuthService.validateTokenAndGetUser(idToken);

    // THEN
    assertThat(result).isEmpty();
  }

  @ParameterizedTest
  @InstancioSource(samples = 5)
  void validateTokenAndGetUser_InterruptedException_ReturnsEmpty(String idToken) throws Exception {
    // GIVEN
    when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenThrow(new InterruptedException("Thread interrupted"));

    // WHEN
    Optional<OAuth2UserInfo> result = googleOAuthService.validateTokenAndGetUser(idToken);

    // THEN
    assertThat(result).isEmpty();
  }

  @ParameterizedTest
  @InstancioSource(samples = 5)
  void validateTokenAndGetUser_JsonProcessingException_ReturnsEmpty(String idToken) throws Exception {
    // GIVEN
    HttpResponse<String> mockResponse = createMockHttpResponse(200, "invalid-json");
    when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenReturn(mockResponse);
    when(objectMapper.readTree("invalid-json"))
        .thenThrow(new JsonProcessingException("Invalid JSON") {});

    // WHEN
    Optional<OAuth2UserInfo> result = googleOAuthService.validateTokenAndGetUser(idToken);

    // THEN
    assertThat(result).isEmpty();
  }

  @ParameterizedTest
  @InstancioSource(samples = 5)
  void validateTokenAndGetUser_UnexpectedException_ReturnsEmpty(String idToken) throws Exception {
    // GIVEN
    HttpResponse<String> mockResponse = createMockHttpResponse(200, "valid-json");
    when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenReturn(mockResponse);
    when(objectMapper.readTree(anyString()))
        .thenThrow(new RuntimeException("Unexpected error"));

    // WHEN
    Optional<OAuth2UserInfo> result = googleOAuthService.validateTokenAndGetUser(idToken);

    // THEN
    assertThat(result).isEmpty();
  }

  // Helper methods

  @SuppressWarnings("unchecked")
  private HttpResponse<String> createMockHttpResponse(int statusCode, String body) {
    HttpResponse<String> mockResponse = mock(HttpResponse.class);
    when(mockResponse.statusCode()).thenReturn(statusCode);
    when(mockResponse.body()).thenReturn(body);
    return mockResponse;
  }

  private ObjectNode createValidTokenInfoResponse(String userId, String email, String name, String pictureUrl) {
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode jsonNode = mapper.createObjectNode();
    jsonNode.put("sub", userId);
    jsonNode.put("aud", TEST_CLIENT_ID);
    jsonNode.put("iss", VALID_ISSUER);
    jsonNode.put("email", email);
    jsonNode.put("name", name);
    jsonNode.put("picture", pictureUrl);
    return jsonNode;
  }

  private ObjectNode createTokenInfoResponseWithoutName(String userId, String email, String givenName, String familyName,
      String pictureUrl) {
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode jsonNode = mapper.createObjectNode();
    jsonNode.put("sub", userId);
    jsonNode.put("aud", TEST_CLIENT_ID);
    jsonNode.put("iss", VALID_ISSUER);
    jsonNode.put("email", email);
    // No "name" field
    jsonNode.put("given_name", givenName);
    jsonNode.put("family_name", familyName);
    jsonNode.put("picture", pictureUrl);
    return jsonNode;
  }

  private ObjectNode createTokenInfoResponseWithGivenNameOnly(String userId, String email, String givenName, String pictureUrl) {
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode jsonNode = mapper.createObjectNode();
    jsonNode.put("sub", userId);
    jsonNode.put("aud", TEST_CLIENT_ID);
    jsonNode.put("iss", VALID_ISSUER);
    jsonNode.put("email", email);
    // No "name" or "family_name" field
    jsonNode.put("given_name", givenName);
    jsonNode.put("picture", pictureUrl);
    return jsonNode;
  }

  private ObjectNode createTokenInfoResponseWithoutAnyName(String userId, String email, String pictureUrl) {
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode jsonNode = mapper.createObjectNode();
    jsonNode.put("sub", userId);
    jsonNode.put("aud", TEST_CLIENT_ID);
    jsonNode.put("iss", VALID_ISSUER);
    jsonNode.put("email", email);
    // No name fields at all
    jsonNode.put("picture", pictureUrl);
    return jsonNode;
  }
}
