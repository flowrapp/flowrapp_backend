package io.github.flowrapp.infrastructure.output.adapters.adapter;

import java.util.Optional;

import io.github.flowrapp.infrastructure.jpa.businessbd.repository.BusinessJpaRepository;
import io.github.flowrapp.infrastructure.output.adapters.mapper.BusinessEntityMapper;
import io.github.flowrapp.model.Business;
import io.github.flowrapp.port.output.BusinessRepositoryOutput;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BusinessRepositoryAdapter implements BusinessRepositoryOutput {

  private final BusinessJpaRepository businessJpaRepository;

  private final BusinessEntityMapper businessEntityMapper;

  @Override
  public Optional<Business> findById(Integer id) {
    return businessJpaRepository.findById(id)
        .map(businessEntityMapper::infra2domain);
  }

  @Override
  public Optional<Business> findByName(String name) {
    return businessJpaRepository.findByName(name)
        .map(businessEntityMapper::infra2domain);
  }

  @Override
  public Business save(Business newBusiness) {
    var jpaNewBusiness = businessJpaRepository.save(
        businessEntityMapper.domain2Infra(newBusiness));

    return businessEntityMapper.infra2domain(jpaNewBusiness);
  }
}
