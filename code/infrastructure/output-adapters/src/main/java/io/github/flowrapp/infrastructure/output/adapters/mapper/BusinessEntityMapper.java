package io.github.flowrapp.infrastructure.output.adapters.mapper;

import io.github.flowrapp.infrastructure.jpa.businessbd.entity.BusinessEntity;
import io.github.flowrapp.model.Business;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR,
    componentModel = ComponentModel.SPRING,
    uses = {UserEntityMapper.class})
public interface BusinessEntityMapper {

  @Mapping(target = "longitude", source = "location.longitude")
  @Mapping(target = "latitude", source = "location.latitude")
  @Mapping(target = "area", source = "location.area")
  @Mapping(target = "members", ignore = true)
  BusinessEntity domain2Infra(Business business);

  @Mapping(target = "location.latitude", source = "latitude")
  @Mapping(target = "location.longitude", source = "longitude")
  @Mapping(target = "location.area", source = "area")
  Business infra2domain(BusinessEntity businessEntity);

}
