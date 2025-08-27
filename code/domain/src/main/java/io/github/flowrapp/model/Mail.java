package io.github.flowrapp.model;

import lombok.Builder;
import org.jspecify.annotations.NonNull;

@Builder(toBuilder = true)
public record Mail(
    @NonNull String recipient,
    @NonNull String subject,
    @NonNull String body) {
}
