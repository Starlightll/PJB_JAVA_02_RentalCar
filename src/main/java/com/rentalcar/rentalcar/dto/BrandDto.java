package com.rentalcar.rentalcar.dto;

import java.io.Serializable;

/**
 * DTO for {@link com.rentalcar.rentalcar.entity.Brand}
 */
public record BrandDto(Integer brandId, String brandName) implements Serializable {
}