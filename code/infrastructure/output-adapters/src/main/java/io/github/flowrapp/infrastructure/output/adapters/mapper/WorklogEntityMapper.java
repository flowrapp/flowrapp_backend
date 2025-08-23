package io.github.flowrapp.infrastructure.output.adapters.mapper;

import java.util.List;

import io.github.flowrapp.infrastructure.jpa.businessbd.entity.WorklogEntity;
import io.github.flowrapp.model.Worklog;
import io.github.flowrapp.value.WorklogFilteredRequest;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR,
    uses = {UserEntityMapper.class, BusinessEntityMapper.class})
public interface WorklogEntityMapper {

  WorklogEntity domain2Infra(Worklog worklog);

  Worklog infra2domain(WorklogEntity worklogEntity);

  List<Worklog> infra2domain(Iterable<WorklogEntity> worklogEntities);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "business.id", source = "businessId")
  @Mapping(target = "user.id", source = "userId")
  @Mapping(target = "clockIn", ignore = true)
  @Mapping(target = "clockOut", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  WorklogEntity domain2Infra(WorklogFilteredRequest worklogFilteredRequest);

  @AfterMapping
  default Worklog finaMapping(Object anySource, @MappingTarget Worklog target) {
    return target.toBusinessZone();
  }

}
