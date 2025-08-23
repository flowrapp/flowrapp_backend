package io.github.flowrapp.value;

import io.github.flowrapp.model.BusinessUserRole;

import lombok.Builder;

@Builder(toBuilder = true)
public record BusinessFilterRequest(
    Integer businessId,
    Integer userId,
    BusinessUserRole role) {
}
