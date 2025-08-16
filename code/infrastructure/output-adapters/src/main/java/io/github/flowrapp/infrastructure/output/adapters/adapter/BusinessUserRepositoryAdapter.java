package io.github.flowrapp.infrastructure.output.adapters.adapter;

import io.github.flowrapp.infrastructure.jpa.businessbd.repository.BusinessUserJpaRepository;
import io.github.flowrapp.infrastructure.output.adapters.mapper.BusinessUserEntityMapper;
import io.github.flowrapp.model.BusinessUser;
import io.github.flowrapp.port.output.BusinessUserRepositoryOutput;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BusinessUserRepositoryAdapter implements BusinessUserRepositoryOutput {

  private final BusinessUserJpaRepository businessUserJpaRepository;

  private final BusinessUserEntityMapper businessUserEntityMapper;

  @Override
  public BusinessUser save(BusinessUser businessUser) {
    val jpaEntity = businessUserJpaRepository.save(
        businessUserEntityMapper.domain2Infra(businessUser));

    return businessUserEntityMapper.infra2domain(jpaEntity);
  }

  @Override
  public boolean userIsMemberOfBusiness(Integer userId, Integer businessId) {
    return businessUserJpaRepository.existsByUser_IdAndBusiness_Id(userId, businessId);
  }

}
