package com.rentalcar.rentalcar.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * DTO for {@link com.rentalcar.rentalcar.entity.CarDraft}
 */
public record CarDraftDto(Integer draftId, String carName, String licensePlate, Date lastModified, String model,
                          String color, Integer seat, Integer productionYear, String transmission, String fuelType,
                          Double basePrice, Double carPrice, String frontImage, String backImage, String leftImage,
                          String rightImage, Integer carId, String verifyStatus, BrandDto brand, UserDto user) implements Serializable {
  /**
   * DTO for {@link com.rentalcar.rentalcar.entity.Brand}
   */
  public record BrandDto(Integer brandId, String brandName) implements Serializable {
  }

    /**
     * DTO for {@link com.rentalcar.rentalcar.entity.User}
     */
    public record UserDto(Long id, String username, String email) implements Serializable {
    }
}