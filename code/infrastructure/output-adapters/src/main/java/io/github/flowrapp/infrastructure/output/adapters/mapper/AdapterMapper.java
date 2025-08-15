package io.github.flowrapp.infrastructure.output.adapters.mapper;

import io.github.flowrapp.infrastructure.jpa.neonazure.entity.MockUserEntity;
import io.github.flowrapp.model.MockUser;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AdapterMapper {

  MockUser jpa2domain(MockUserEntity mockUserEntity);
}
