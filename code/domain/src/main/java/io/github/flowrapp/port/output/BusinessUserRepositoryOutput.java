package io.github.flowrapp.port.output;

import java.util.List;
import java.util.Optional;

import io.github.flowrapp.model.BusinessUser;
import io.github.flowrapp.value.BusinessFilterRequest;

public interface BusinessUserRepositoryOutput {

  Optional<BusinessUser> getByUserAndBusinessId(Integer userId, Integer businessId);

  List<BusinessUser> findByUser(Integer id);

  List<BusinessUser> findByFilter(BusinessFilterRequest filter);

  BusinessUser save(BusinessUser businessUser);

  boolean userIsMemberOfBusiness(Integer userId, Integer businessId);
}
