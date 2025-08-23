package io.github.flowrapp.infrastructure.output.adapters.mapper;

import java.util.List;

import io.github.flowrapp.infrastructure.jpa.businessbd.entity.InvitationEntity;
import io.github.flowrapp.model.Invitation;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
   
    uses = {UserEntityMapper.class, BusinessEntityMapper.class})
public interface InvitationEntityMapper {

  InvitationEntity domain2Infra(Invitation invitation);

  Invitation infra2domain(InvitationEntity invitationEntity);

  List<Invitation> infra2domain(Iterable<InvitationEntity> invitationEntities);
}
