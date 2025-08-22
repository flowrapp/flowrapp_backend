package io.github.flowrapp.infrastructure.input.rest.mainapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

/**
 * Request DTO for GitHub OAuth2 authentication. Contains the GitHub access token obtained from the frontend OAuth2 flow.
 */
public record GitHubOAuthRequestDTO(
    @JsonProperty("access_token") @NotBlank(message = "Access token is required") String accessToken) {
}
