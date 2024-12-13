package com.rentalcar.rentalcar.mappers;

import com.rentalcar.rentalcar.dto.CarDto1;
import com.rentalcar.rentalcar.entity.Car;
import com.rentalcar.rentalcar.repository.CarRepository;
import com.rentalcar.rentalcar.service.SearchCarService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;


@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class CarMapper {

    @Autowired
    private CarRepository carRepository;

    @Mapping(target = "rateAverage", expression = "java(calculateRateAverage(car))")
    public abstract CarDto1 toDto(Car car);

    // Helper method to calculate rate average
    Double calculateRateAverage(Car car) {
        Double rateAverage = carRepository.getRateAvgByCarId(car.getCarId());
        return rateAverage != null ? rateAverage : 0.0;
    }


}