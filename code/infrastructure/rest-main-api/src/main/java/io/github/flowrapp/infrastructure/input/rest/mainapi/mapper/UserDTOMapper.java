package io.github.flowrapp.infrastructure.input.rest.mainapi.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface UserDTOMapper {

}
