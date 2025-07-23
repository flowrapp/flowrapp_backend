package io.github.flowrapp.infrastructure.output.adapters.mapper;

import io.github.flowrapp.infrastructure.jpa.main.postgres.entity.UserEntity;
import io.github.flowrapp.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AdapterMapper {

  User jpa2domain(UserEntity userEntity);
}
