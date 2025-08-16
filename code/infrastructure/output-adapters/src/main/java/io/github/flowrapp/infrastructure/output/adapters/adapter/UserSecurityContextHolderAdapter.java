package io.github.flowrapp.infrastructure.output.adapters.adapter;

import java.util.Optional;

import io.github.flowrapp.infrastructure.input.rest.mainapi.security.ClaimConstants;
import io.github.flowrapp.model.User;
import io.github.flowrapp.port.output.UserSecurityContextHolderOutput;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserSecurityContextHolderAdapter implements UserSecurityContextHolderOutput {

  private final UserRepositoryAdapter userRepositoryAdapter;

  @Override
  public Optional<String> getCurrentUserEmail() {
    return Optional.ofNullable(
        switch (SecurityContextHolder.getContext().getAuthentication().getPrincipal()) {
          case org.springframework.security.core.userdetails.User user -> user.getUsername();
          case Jwt jwt -> jwt.getClaimAsString(ClaimConstants.CLAIM_KEY_USER_MAIL);
          default -> {
            log.warn("Current user principal is not a String or User type: {}",
                SecurityContextHolder.getContext().getAuthentication().getPrincipal());
            yield null;
          }
        });
  }

  @Override
  public Optional<User> getCurrentUser() {
    return getCurrentUserEmail()
        .flatMap(userRepositoryAdapter::findUserByEmail);
  }
}
