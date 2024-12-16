package com.rentalcar.rentalcar.mappers;

import com.rentalcar.rentalcar.dto.BrandDto;
import com.rentalcar.rentalcar.entity.Brand;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface BrandMapper {
    BrandDto toDto(Brand brand);
}