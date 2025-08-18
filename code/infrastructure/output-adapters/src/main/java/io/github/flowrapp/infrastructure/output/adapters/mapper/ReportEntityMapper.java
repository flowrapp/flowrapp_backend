package io.github.flowrapp.infrastructure.output.adapters.mapper;

import java.util.List;

import io.github.flowrapp.infrastructure.jpa.businessbd.entity.ReportEntity;
import io.github.flowrapp.model.Report;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = ComponentModel.SPRING,
    uses = {UserEntityMapper.class, BusinessEntityMapper.class})
public interface ReportEntityMapper {

  @Mapping(target = "clockDay", source = "day")
  ReportEntity domain2Infra(Report report);

  @Mapping(target = "day", source = "clockDay")
  Report infra2domain(ReportEntity reportEntity);

  List<Report> infra2domain(Iterable<ReportEntity> reportEntities);

}
