package io.github.flowrapp.infrastructure.input.rest.mainapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

/**
 * Request DTO for Google OAuth2 authentication. Contains the Google ID token obtained from the frontend OAuth2 flow.
 */
public record GoogleOAuthRequestDTO(
    @JsonProperty("id_token") @NotBlank(message = "ID token is required") String idToken) {
}
