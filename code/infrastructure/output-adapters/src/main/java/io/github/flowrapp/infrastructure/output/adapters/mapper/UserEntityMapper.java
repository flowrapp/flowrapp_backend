package io.github.flowrapp.infrastructure.output.adapters.mapper;

import io.github.flowrapp.infrastructure.jpa.businessbd.entity.UserEntity;
import io.github.flowrapp.model.User;
import io.github.flowrapp.value.SensitiveInfo;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface UserEntityMapper {

  @Mapping(target = "ownedBusinesses", ignore = true)
  @Mapping(target = "businessMemberships", ignore = true)
  UserEntity domain2Infra(User user);

  User infra2domain(UserEntity userEntity);

  default SensitiveInfo<String> map(String passwordHash) {
    return SensitiveInfo.of(passwordHash);
  }

  default String map(SensitiveInfo<String> passwordHash) {
    return passwordHash == null ? null : passwordHash.get();
  }

}
