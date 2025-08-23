package io.github.flowrapp.infrastructure.output.adapters.mapper;

import java.util.List;

import io.github.flowrapp.infrastructure.jpa.businessbd.entity.BusinessUserEntity;
import io.github.flowrapp.model.BusinessUser;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR,
   
    uses = {UserEntityMapper.class, BusinessEntityMapper.class})
public interface BusinessUserEntityMapper {

  @Mapping(target = "id.userId", source = "user.id")
  @Mapping(target = "id.businessId", source = "business.id")
  BusinessUserEntity domain2Infra(BusinessUser businessUser);

  BusinessUser infra2domain(BusinessUserEntity businessUserEntity);

  List<BusinessUser> infra2domain(Iterable<BusinessUserEntity> businessUserEntities);

}
