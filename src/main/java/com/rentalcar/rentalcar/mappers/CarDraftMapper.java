package com.rentalcar.rentalcar.mappers;

import com.rentalcar.rentalcar.dto.CarDraftDto;
import com.rentalcar.rentalcar.entity.CarDraft;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)public interface CarDraftMapper {
    CarDraftDto toDto(CarDraft carDraft);
}