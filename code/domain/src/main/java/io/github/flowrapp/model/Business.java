package io.github.flowrapp.model;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Objects;

import io.github.flowrapp.value.BusinessCreationRequest;

import lombok.Builder;

@Builder(toBuilder = true)
public record Business(
    Integer id,
    String name,
    User owner,
    Location location,
    ZoneId timezoneOffset,
    Instant createdAt) {

  public boolean isOwner(User currentUser) {
    return Objects.equals(this.owner.id(), currentUser.id());
  }

  /**
   * Creates a new business from a business creation request and owner.
   *
   * @param businessCreationRequest the request containing business information
   * @param owner the user who owns this business
   * @return a new business instance (not yet persisted)
   */
  public static Business fromBusinessCreationRequest(BusinessCreationRequest businessCreationRequest, User owner) {
    return Business.builder()
        .name(businessCreationRequest.name())
        .location(businessCreationRequest.location())
        .owner(owner)
        .timezoneOffset(businessCreationRequest.timezoneOffset())
        .createdAt(Instant.now())
        .build();
  }

}
