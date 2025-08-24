package io.github.flowrapp.infrastructure.output.adapters.mapper;

import java.util.List;

import io.github.flowrapp.infrastructure.jpa.businessbd.entity.BusinessUserEntity;
import io.github.flowrapp.model.BusinessUser;
import io.github.flowrapp.value.BusinessFilterRequest;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR,

    uses = {UserEntityMapper.class, BusinessEntityMapper.class})
public interface BusinessUserEntityMapper {

  @Mapping(target = "id.userId", source = "user.id")
  @Mapping(target = "id.businessId", source = "business.id")
  BusinessUserEntity domain2Infra(BusinessUser businessUser);

  BusinessUser infra2domain(BusinessUserEntity businessUserEntity);

  List<BusinessUser> infra2domain(Iterable<BusinessUserEntity> businessUserEntities);

  @Mapping(target = "user.id", source = "userId")
  @Mapping(target = "business.id", source = "businessId")
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "joinedAt", ignore = true)
  @Mapping(target = "invitedBy", ignore = true)
  BusinessUserEntity filter2example(BusinessFilterRequest filter);

}
