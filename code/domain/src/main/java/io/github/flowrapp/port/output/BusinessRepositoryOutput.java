package io.github.flowrapp.port.output;

import java.util.Optional;

import io.github.flowrapp.model.Business;

public interface BusinessRepositoryOutput {

  Optional<Business> findByName(String name);

  Business save(Business newBusiness);
}
