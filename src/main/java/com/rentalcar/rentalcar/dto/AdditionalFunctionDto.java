package com.rentalcar.rentalcar.dto;

import java.io.Serializable;

/**
 * DTO for {@link com.rentalcar.rentalcar.entity.AdditionalFunction}
 */
public record AdditionalFunctionDto(Integer functionId, String functionName) implements Serializable {
}