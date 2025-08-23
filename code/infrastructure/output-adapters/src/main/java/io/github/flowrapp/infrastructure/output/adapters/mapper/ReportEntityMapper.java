package io.github.flowrapp.infrastructure.output.adapters.mapper;

import java.util.List;

import io.github.flowrapp.infrastructure.jpa.businessbd.entity.ReportEntity;
import io.github.flowrapp.model.Report;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR,
    uses = {UserEntityMapper.class, BusinessEntityMapper.class})
public interface ReportEntityMapper {

  @Mapping(target = "id.clockDay", source = "day")
  @Mapping(target = "id.userId", source = "user.id")
  @Mapping(target = "id.businessId", source = "business.id")
  ReportEntity domain2Infra(Report report);

  @Mapping(target = "day", source = "id.clockDay")
  Report infra2domain(ReportEntity reportEntity);

  List<Report> infra2domain(Iterable<ReportEntity> reportEntities);

}
