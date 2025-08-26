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

import io.github.flowrapp.infrastructure.input.rest.mainapi.security.value.GitHubUserInfo;
import io.github.flowrapp.value.OAuth2UserInfo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
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
class GitHubOAuthServiceTest {

  @Mock
  private HttpClient httpClient;

  @Mock
  private ObjectMapper objectMapper;

  private GitHubOAuthService gitHubOAuthService;

  @BeforeEach
  void setUp() {
    gitHubOAuthService = new GitHubOAuthService();
    // Inject mocked dependencies using reflection
    ReflectionTestUtils.setField(gitHubOAuthService, "httpClient", httpClient);
    ReflectionTestUtils.setField(gitHubOAuthService, "objectMapper", objectMapper);
  }

  @ParameterizedTest
  @InstancioSource(samples = 5)
  void validateTokenAndGetUser_SuccessfulValidation_WithPrimaryEmailFromApi(String accessToken, long userId, String email, String name,
      String login, String avatarUrl) throws Exception {
    // GIVEN
    HttpResponse<String> userResponse = createMockHttpResponse(200, "user-json");
    HttpResponse<String> emailsResponse = createMockHttpResponse(200, "emails-json");
    when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenReturn(userResponse, emailsResponse);

    ObjectNode userNode = createGitHubUserResponse(userId, login, name, avatarUrl, null);
    ArrayNode emailsNode = createGitHubEmailsResponse(email, true, true);
    when(objectMapper.readTree("user-json")).thenReturn(userNode);
    when(objectMapper.readTree("emails-json")).thenReturn(emailsNode);

    // WHEN
    Optional<OAuth2UserInfo> result = gitHubOAuthService.validateTokenAndGetUser(accessToken);

    // THEN
    assertThat(result).isPresent();
    OAuth2UserInfo userInfo = result.get();
    assertThat(userInfo).isInstanceOf(GitHubUserInfo.class);
    assertThat(userInfo.getId()).isEqualTo(String.valueOf(userId));
    assertThat(userInfo.getEmail()).isEqualTo(email);
    assertThat(userInfo.getName()).isEqualTo(name);
    assertThat(userInfo.getAvatarUrl()).isEqualTo(avatarUrl);
    assertThat(userInfo.getProvider()).isEqualTo(OAuth2UserInfo.Provider.GITHUB);
  }

  @ParameterizedTest
  @InstancioSource(samples = 5)
  void validateTokenAndGetUser_SuccessfulValidation_WithVerifiedEmailFallback(String accessToken, long userId, String email, String name,
      String login, String avatarUrl) throws Exception {
    // GIVEN
    HttpResponse<String> userResponse = createMockHttpResponse(200, "user-json");
    HttpResponse<String> emailsResponse = createMockHttpResponse(200, "emails-json");
    when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenReturn(userResponse, emailsResponse);

    ObjectNode userNode = createGitHubUserResponse(userId, login, name, avatarUrl, null);
    ArrayNode emailsNode = createGitHubEmailsResponse(email, false, true); // Not primary but verified
    when(objectMapper.readTree("user-json")).thenReturn(userNode);
    when(objectMapper.readTree("emails-json")).thenReturn(emailsNode);

    // WHEN
    Optional<OAuth2UserInfo> result = gitHubOAuthService.validateTokenAndGetUser(accessToken);

    // THEN
    assertThat(result).isPresent();
    assertThat(result.get().getEmail()).isEqualTo(email);
  }

  @ParameterizedTest
  @InstancioSource(samples = 5)
  void validateTokenAndGetUser_SuccessfulValidation_WithPublicEmailFallback(String accessToken, long userId, String publicEmail,
      String name,
      String login, String avatarUrl) throws Exception {
    // GIVEN
    HttpResponse<String> userResponse = createMockHttpResponse(200, "user-json");
    HttpResponse<String> emailsResponse = createMockHttpResponse(403, "forbidden"); // No access to emails API
    when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenReturn(userResponse, emailsResponse);

    ObjectNode userNode = createGitHubUserResponse(userId, login, name, avatarUrl, publicEmail);
    when(objectMapper.readTree("user-json")).thenReturn(userNode);

    // WHEN
    Optional<OAuth2UserInfo> result = gitHubOAuthService.validateTokenAndGetUser(accessToken);

    // THEN
    assertThat(result).isPresent();
    assertThat(result.get().getEmail()).isEqualTo(publicEmail);
  }

  @ParameterizedTest
  @InstancioSource(samples = 5)
  void validateTokenAndGetUser_SuccessfulValidation_WithLoginFallbackForName(String accessToken, long userId, String email,
      String login, String avatarUrl) throws Exception {
    // GIVEN
    HttpResponse<String> userResponse = createMockHttpResponse(200, "user-json");
    HttpResponse<String> emailsResponse = createMockHttpResponse(200, "emails-json");
    when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenReturn(userResponse, emailsResponse);

    ObjectNode userNode = createGitHubUserResponse(userId, login, null, avatarUrl, null); // No name provided
    ArrayNode emailsNode = createGitHubEmailsResponse(email, true, true);
    when(objectMapper.readTree("user-json")).thenReturn(userNode);
    when(objectMapper.readTree("emails-json")).thenReturn(emailsNode);

    // WHEN
    Optional<OAuth2UserInfo> result = gitHubOAuthService.validateTokenAndGetUser(accessToken);

    // THEN
    assertThat(result).isPresent();
    assertThat(result.get().getName()).isEqualTo(login); // Should fallback to login
  }

  @ParameterizedTest
  @InstancioSource(samples = 5)
  void validateTokenAndGetUser_SuccessfulValidation_NoEmailAvailable(String accessToken, long userId, String name,
      String login, String avatarUrl) throws Exception {
    // GIVEN
    HttpResponse<String> userResponse = createMockHttpResponse(200, "user-json");
    HttpResponse<String> emailsResponse = createMockHttpResponse(403, "forbidden");
    when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenReturn(userResponse, emailsResponse);

    ObjectNode userNode = createGitHubUserResponse(userId, login, name, avatarUrl, null); // No public email
    when(objectMapper.readTree("user-json")).thenReturn(userNode);

    // WHEN
    Optional<OAuth2UserInfo> result = gitHubOAuthService.validateTokenAndGetUser(accessToken);

    // THEN
    assertThat(result).isPresent();
    assertThat(result.get().getEmail()).isNull();
  }

  @ParameterizedTest
  @InstancioSource(samples = 3)
  void validateTokenAndGetUser_UserApiError_ReturnsEmpty(String accessToken, int errorStatus) throws Exception {
    // GIVEN
    HttpResponse<String> userResponse = createMockHttpResponse(errorStatus, "error-response");
    when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenReturn(userResponse);

    // WHEN
    Optional<OAuth2UserInfo> result = gitHubOAuthService.validateTokenAndGetUser(accessToken);

    // THEN
    assertThat(result).isEmpty();
  }

  @ParameterizedTest
  @InstancioSource(samples = 5)
  void validateTokenAndGetUser_IOException_ReturnsEmpty(String accessToken) throws Exception {
    // GIVEN
    when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenThrow(new IOException("Network error"));

    // WHEN
    Optional<OAuth2UserInfo> result = gitHubOAuthService.validateTokenAndGetUser(accessToken);

    // THEN
    assertThat(result).isEmpty();
  }

  @ParameterizedTest
  @InstancioSource(samples = 5)
  void validateTokenAndGetUser_InterruptedException_ReturnsEmpty(String accessToken) throws Exception {
    // GIVEN
    when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenThrow(new InterruptedException("Thread interrupted"));

    // WHEN
    Optional<OAuth2UserInfo> result = gitHubOAuthService.validateTokenAndGetUser(accessToken);

    // THEN
    assertThat(result).isEmpty();
  }

  @ParameterizedTest
  @InstancioSource(samples = 5)
  void validateTokenAndGetUser_JsonProcessingException_ReturnsEmpty(String accessToken) throws Exception {
    // GIVEN
    HttpResponse<String> userResponse = createMockHttpResponse(200, "invalid-json");
    when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenReturn(userResponse);
    when(objectMapper.readTree("invalid-json"))
        .thenThrow(new JsonProcessingException("Invalid JSON") {});

    // WHEN
    Optional<OAuth2UserInfo> result = gitHubOAuthService.validateTokenAndGetUser(accessToken);

    // THEN
    assertThat(result).isEmpty();
  }

  @ParameterizedTest
  @InstancioSource(samples = 5)
  void validateTokenAndGetUser_UnexpectedException_ReturnsEmpty(String accessToken) throws Exception {
    // GIVEN
    HttpResponse<String> userResponse = createMockHttpResponse(200, "user-json");
    when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenReturn(userResponse);
    when(objectMapper.readTree(anyString()))
        .thenThrow(new RuntimeException("Unexpected error"));

    // WHEN
    Optional<OAuth2UserInfo> result = gitHubOAuthService.validateTokenAndGetUser(accessToken);

    // THEN
    assertThat(result).isEmpty();
  }

  @Test
  void validateTokenAndGetUser_FirstEmailFallback_WhenNoPrimaryOrVerified() throws Exception {
    // GIVEN
    String accessToken = "valid-token";
    String firstEmail = "first@example.com";

    HttpResponse<String> userResponse = createMockHttpResponse(200, "user-json");
    HttpResponse<String> emailsResponse = createMockHttpResponse(200, "emails-json");
    when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenReturn(userResponse, emailsResponse);

    ObjectNode userNode = createGitHubUserResponse(123L, "testuser", "Test User", "http://avatar.url", null);
    ArrayNode emailsNode = createGitHubEmailsResponse(firstEmail, false, false); // Neither primary nor verified
    when(objectMapper.readTree("user-json")).thenReturn(userNode);
    when(objectMapper.readTree("emails-json")).thenReturn(emailsNode);

    // WHEN
    Optional<OAuth2UserInfo> result = gitHubOAuthService.validateTokenAndGetUser(accessToken);

    // THEN
    assertThat(result).isPresent();
    assertThat(result.get().getEmail()).isEqualTo(firstEmail);
  }

  @Test
  void validateTokenAndGetUser_EmptyEmailsArray_FallsBackToPublicEmail() throws Exception {
    // GIVEN
    String accessToken = "valid-token";
    String publicEmail = "public@example.com";

    HttpResponse<String> userResponse = createMockHttpResponse(200, "user-json");
    HttpResponse<String> emailsResponse = createMockHttpResponse(200, "emails-json");
    when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenReturn(userResponse, emailsResponse);

    ObjectNode userNode = createGitHubUserResponse(123L, "testuser", "Test User", "http://avatar.url", publicEmail);
    ArrayNode emailsNode = new ObjectMapper().createArrayNode(); // Empty array
    when(objectMapper.readTree("user-json")).thenReturn(userNode);
    when(objectMapper.readTree("emails-json")).thenReturn(emailsNode);

    // WHEN
    Optional<OAuth2UserInfo> result = gitHubOAuthService.validateTokenAndGetUser(accessToken);

    // THEN
    assertThat(result).isPresent();
    assertThat(result.get().getEmail()).isEqualTo(publicEmail);
  }

  // Helper methods

  @SuppressWarnings("unchecked")
  private HttpResponse<String> createMockHttpResponse(int statusCode, String body) {
    HttpResponse<String> mockResponse = mock(HttpResponse.class);
    when(mockResponse.statusCode()).thenReturn(statusCode);
    when(mockResponse.body()).thenReturn(body);
    return mockResponse;
  }

  private ObjectNode createGitHubUserResponse(long userId, String login, String name, String avatarUrl, String publicEmail) {
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode userNode = mapper.createObjectNode();
    userNode.put("id", userId);
    userNode.put("login", login);

    if (name != null) {
      userNode.put("name", name);
    }

    if (avatarUrl != null) {
      userNode.put("avatar_url", avatarUrl);
    }

    if (publicEmail != null) {
      userNode.put("email", publicEmail);
    }

    return userNode;
  }

  private ArrayNode createGitHubEmailsResponse(String email, boolean primary, boolean verified) {
    ObjectMapper mapper = new ObjectMapper();
    ArrayNode emailsArray = mapper.createArrayNode();
    ObjectNode emailNode = mapper.createObjectNode();

    emailNode.put("email", email);
    emailNode.put("primary", primary);
    emailNode.put("verified", verified);

    emailsArray.add(emailNode);
    return emailsArray;
  }
}
