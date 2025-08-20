package io.github.flowrapp.port.input;

import java.util.List;

import io.github.flowrapp.model.BusinessUser;
import io.github.flowrapp.value.BusinessFilterRequest;

public interface BusinessUseCase {

  List<BusinessUser> getUserBusiness();

  List<BusinessUser> getBusinessUsers(BusinessFilterRequest filter);

}
