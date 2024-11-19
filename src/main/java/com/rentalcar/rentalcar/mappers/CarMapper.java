package com.rentalcar.rentalcar.mappers;

import com.rentalcar.rentalcar.dto.CarDto1;
import com.rentalcar.rentalcar.entity.Car;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CarMapper {
    CarDto1 toDto(Car car);
}