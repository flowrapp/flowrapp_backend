package io.github.flowrapp.infrastructure.output.adapters.adapter;

import java.util.List;
import java.util.Optional;

import io.github.flowrapp.infrastructure.jpa.businessbd.repository.BusinessUserJpaRepository;
import io.github.flowrapp.infrastructure.output.adapters.mapper.BusinessUserEntityMapper;
import io.github.flowrapp.model.BusinessUser;
import io.github.flowrapp.port.output.BusinessUserRepositoryOutput;
import io.github.flowrapp.value.BusinessFilterRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BusinessUserRepositoryAdapter implements BusinessUserRepositoryOutput {

  private final BusinessUserJpaRepository businessUserJpaRepository;

  private final BusinessUserEntityMapper businessUserEntityMapper;

  @Override
  public Optional<BusinessUser> getByUserAndBusinessId(Integer userId, Integer businessId) {
    return businessUserJpaRepository.findByUser_IdAndBusiness_Id(userId, businessId)
        .map(businessUserEntityMapper::infra2domain);
  }

  @Override
  public List<BusinessUser> findByUser(Integer id) {
    return businessUserEntityMapper.infra2domain(
        businessUserJpaRepository.findByUser_Id(id));
  }

  @Override
  public List<BusinessUser> findByFilter(BusinessFilterRequest filter) {
    var example = Example.of(
        businessUserEntityMapper.filter2example(filter));

    return businessUserEntityMapper.infra2domain(
        businessUserJpaRepository.findAll(example));
  }

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
