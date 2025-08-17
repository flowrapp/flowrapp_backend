package io.github.flowrapp.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enum with the error codes and messages.
 */
@Getter
@RequiredArgsConstructor
public enum FunctionalError {
  USER_NOT_FOUND(1000, 404, "user not found"),
  ADMIN_USER_NOT_FOUND(1001, 500, "admin user not found"),
  USERNAME_ALREADY_EXISTS(1002, 409, "user already exists"),
  INVALID_CREDENTIALS(1003, 401, "invalid credentials"),
  INVALID_REFRESH_TOKEN(1004, 401, "invalid refresh token"),
  INVITATION_NOT_FOUND(1005, 404, "invitation not found"),
  BUSINESS_NOT_FOUND(1006, 404, "business does not exist"),
  USER_ALREADY_MEMBER_OF_BUSINESS(1007, 409, "user already member of business"),
  INVITATION_ALREADY_EXISTS(1008, 409, "user is already invited to this business"),
  INVITATION_NOT_FOR_CURRENT_USER(1009, 403, "invitation not for current user"),
  INVITATION_EXPIRED(1010, 403, "invitation expired"),
  USER_INVITATION_NOT_OWNER(1011, 403, "user not authorized for invitation"),
  INVITATION_ALREADY_ACCEPTED(1012, 403, "invitation already accepted"),
  INVITATION_NOT_PENDING(1013, 403, "invitation not pending"),
  USER_ALREADY_ENABLED(1014, 409, "user already enabled"),
  USER_IS_NOT_MEMBER_OF_BUSINESS(1015, 403, "user is not member of business"),
  CURRENT_USER_NOT_FOUND(1016, 500, "Current user not found"),
  WORKLOG_NOT_FOUND(1017, 404, "worklog not found"),
  WORKLOG_CLOSED(1018, 403, "worklog already closed"),
  WORKLOG_NOT_OWNER(1019, 403, "user is not the owner of worklog"),
  WORKLOG_NOT_VALID(1019, 400, "worklog is not valid"),
  USER_NOT_OWNER_OF_BUSINESS(1020, 403, "user is not the owner of business"),
  ;

  private final int code;

  private final int status;

  private final String message;

}
