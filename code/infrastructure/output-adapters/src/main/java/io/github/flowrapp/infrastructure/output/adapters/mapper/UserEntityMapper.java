package io.github.flowrapp.infrastructure.output.adapters.mapper;

import io.github.flowrapp.infrastructure.jpa.businessBd.entity.UserEntity;
import io.github.flowrapp.model.User;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = ComponentModel.SPRING)
public interface UserEntityMapper {

  UserEntity domain2Infra(User user);

  User infra2domain(UserEntity userEntity);

}
