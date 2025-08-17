package io.github.flowrapp.infrastructure.output.adapters.mapper;

import java.util.List;

import io.github.flowrapp.infrastructure.jpa.businessbd.entity.WorklogEntity;
import io.github.flowrapp.model.Worklog;
import io.github.flowrapp.model.value.WorklogFilteredRequest;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR,
    componentModel = ComponentModel.SPRING, uses = {UserEntityMapper.class, BusinessEntityMapper.class})
public interface WorklogEntityMapper {

  WorklogEntity domain2Infra(Worklog worklog);

  Worklog infra2domain(WorklogEntity worklogEntity);

  List<Worklog> infra2domain(List<WorklogEntity> worklogEntities);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "business.id", source = "businessId")
  @Mapping(target = "user.id", source = "userId")
  @Mapping(target = "clockIn", ignore = true)
  @Mapping(target = "clockOut", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  WorklogEntity domain2Infra(WorklogFilteredRequest worklogFilteredRequest);
}
