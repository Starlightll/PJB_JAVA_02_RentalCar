package com.rentalcar.rentalcar.dto;

import java.io.Serializable;
import java.util.Set;

/**
 * DTO for {@link com.rentalcar.rentalcar.entity.Brand}
 */
public record BrandDto(Integer brandId, String brandName, Set<CarDto> cars) implements Serializable {
    /**
     * DTO for {@link com.rentalcar.rentalcar.entity.Car}
     */
    public record CarDto(Integer carId, String carName, CarStatusDto carStatus) implements Serializable {
        /**
         * DTO for {@link com.rentalcar.rentalcar.entity.CarStatus}
         */
        public record CarStatusDto(Integer statusId, String name) implements Serializable {
        }
    }
}