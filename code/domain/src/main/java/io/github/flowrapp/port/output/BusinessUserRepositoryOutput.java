package io.github.flowrapp.port.output;

import io.github.flowrapp.model.BusinessUser;

public interface BusinessUserRepositoryOutput {

  BusinessUser save(BusinessUser businessUser);

}
