package com.rentalcar.rentalcar.mappers;

import com.rentalcar.rentalcar.dto.AdditionalFunctionDto;
import com.rentalcar.rentalcar.entity.AdditionalFunction;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface AdditionalFunctionMapper {
    AdditionalFunctionDto toDto(AdditionalFunction additionalFunction);
}