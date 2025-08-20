package io.github.flowrapp.usecase;

import java.util.List;

import io.github.flowrapp.model.BusinessUser;
import io.github.flowrapp.port.input.BusinessUseCase;
import io.github.flowrapp.port.output.BusinessUserRepositoryOutput;
import io.github.flowrapp.port.output.UserSecurityContextHolderOutput;
import io.github.flowrapp.value.BusinessFilterRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BusinessUseCaseImpl implements BusinessUseCase {

  private final BusinessUserRepositoryOutput businessUserRepositoryOutput;

  private final UserSecurityContextHolderOutput userSecurityContextHolderOutput;

  @Override
  public List<BusinessUser> getUserBusiness() {
    val currentUser = userSecurityContextHolderOutput.getCurrentUser();
    log.debug("Getting businesses for user: {}", currentUser.id());

    return businessUserRepositoryOutput.findByUser(currentUser.id());
  }

  @Override
  public List<BusinessUser> getBusinessUsers(BusinessFilterRequest filter) {
    log.debug("Getting business users with filter: {}", filter);

    return businessUserRepositoryOutput.findByFilter(filter);
  }
}
