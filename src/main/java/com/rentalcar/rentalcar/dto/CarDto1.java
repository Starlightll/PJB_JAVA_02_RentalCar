package com.rentalcar.rentalcar.dto;

import com.rentalcar.rentalcar.common.UserStatus;
import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.Set;

/**
 * DTO for {@link com.rentalcar.rentalcar.entity.Car}
 */
@Value
public class CarDto1 implements Serializable {
    Integer carId;
    String carName;
    String licensePlate;
    String model;
    String color;
    Integer seat;
    Integer productionYear;
    String transmission;
    String fuelType;
    Double mileage;
    Double fuelConsumption;
    Double basePrice;
    Double deposit;
    String description;
    String terms;
    Double carPrice;
    String frontImage;
    String backImage;
    String leftImage;
    String rightImage;
    String registration;
    String certificate;
    String insurance;
    Date lastModified;
    UserDto user;
    BrandDto brand;
    CarStatusDto carStatus;
    Set<AdditionalFunctionDto> additionalFunctions;
    CarAddressDto address;
    Double rateAverage;
    Long bookedTimes;
    Integer currentStatus;
    /**
     * DTO for {@link com.rentalcar.rentalcar.entity.User}
     */
    @Value
    public static class UserDto implements Serializable {
        Long id;
        String username;
        LocalDate dob;
        String email;
        String nationalId;
        String phone;
        String drivingLicense;
        BigDecimal wallet;
        String city;
        String district;
        String ward;
        String street;
        String fullName;
        boolean agreeTerms;
        UserStatus status;
        Integer statusDriverId;
    }

    /**
     * DTO for {@link com.rentalcar.rentalcar.entity.Brand}
     */
    @Value
    public static class BrandDto implements Serializable {
        Integer brandId;
        String brandName;
    }

    /**
     * DTO for {@link com.rentalcar.rentalcar.entity.CarStatus}
     */
    @Value
    public static class CarStatusDto implements Serializable {
        Integer statusId;
        String name;
    }

    /**
     * DTO for {@link com.rentalcar.rentalcar.entity.AdditionalFunction}
     */
    public record AdditionalFunctionDto(Integer functionId, String functionName) implements Serializable {
    }

    /**
     * DTO for {@link com.rentalcar.rentalcar.entity.CarAddress}
     */
    public record CarAddressDto(Integer addressId, Integer provinceId, String province, Integer districtId, String district, Integer wardId, String ward, String street) implements Serializable {
    }
}