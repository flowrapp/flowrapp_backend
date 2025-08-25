package io.github.flowrapp.model;

import lombok.Builder;

@Builder(toBuilder = true)
public record Mail(
    String recipient,
    String subject,
    String body) {
}
