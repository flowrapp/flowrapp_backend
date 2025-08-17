package io.github.flowrapp.port.output;

import java.util.Optional;

import io.github.flowrapp.model.BusinessUser;

public interface BusinessUserRepositoryOutput {

  Optional<BusinessUser> getByUserAndBusinessId(Integer userId, Integer businessId);

  BusinessUser save(BusinessUser businessUser);

  boolean userIsMemberOfBusiness(Integer userId, Integer businessId);

}
