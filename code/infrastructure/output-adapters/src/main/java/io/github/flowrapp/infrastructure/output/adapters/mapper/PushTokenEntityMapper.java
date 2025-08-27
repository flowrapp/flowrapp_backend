package io.github.flowrapp.infrastructure.output.adapters.mapper;

import io.github.flowrapp.infrastructure.jpa.businessbd.entity.PushTokenEntity;
import io.github.flowrapp.model.PushToken;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
    uses = {UserEntityMapper.class})
public interface PushTokenEntityMapper {

  PushToken infra2domain(PushTokenEntity pushTokenEntity);

  PushTokenEntity domain2Infra(PushToken pushToken);

}
