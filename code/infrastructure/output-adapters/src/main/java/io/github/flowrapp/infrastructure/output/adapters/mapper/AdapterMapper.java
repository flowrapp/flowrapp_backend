package io.github.flowrapp.infrastructure.output.adapters.mapper;

import io.github.flowrapp.infrastructure.jpa.businessbd.entity.MockUserEntity;
import io.github.flowrapp.model.MockUser;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface AdapterMapper {

  MockUser jpa2domain(MockUserEntity mockUserEntity);
}
