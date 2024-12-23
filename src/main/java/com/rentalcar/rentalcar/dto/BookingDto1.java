package com.rentalcar.rentalcar.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;

/**
 * DTO for {@link com.rentalcar.rentalcar.entity.Booking}
 */
public record BookingDto1(Long bookingId, LocalDateTime startDate, LocalDateTime endDate, String driverInfo,
                          LocalDateTime actualEndDate, Double deposit, Double totalPrice, Date lastModified,
                          UserDto1 user, BookingStatusDto bookingStatus,
                          PaymentMethodDto paymentMethod, Double basePrice, Double additionalPrice, Double discount,
                          Double finalPrice, CarDto car) implements Serializable {
    /**
     * DTO for {@link com.rentalcar.rentalcar.entity.User}
     */
    public record UserDto1(Long id, String username, String avatar, LocalDate dob, String email, String nationalId,
                           String phone, String drivingLicense, String city, String district, String ward,
                           String street, String fullName) implements Serializable {
    }

    /**
     * DTO for {@link com.rentalcar.rentalcar.entity.BookingStatus}
     */
    public record BookingStatusDto(Long bookingStatusId, String name) implements Serializable {
    }

    /**
     * DTO for {@link com.rentalcar.rentalcar.entity.PaymentMethod}
     */
    public record PaymentMethodDto(Long paymentMethodId, String name) implements Serializable {
    }

    /**
     * DTO for {@link com.rentalcar.rentalcar.entity.Car}
     */
    public record CarDto(Integer carId, String carName, String licensePlate, String model, String color, Integer seat, Integer productionYear, String transmission, String fuelType, Double mileage, Double fuelConsumption, Double basePrice, Double deposit, String description, String terms, Double carPrice, String frontImage, String backImage, String leftImage, String rightImage, String registration, String certificate, String insurance, Date lastModified, Set<AdditionalFunctionDto> additionalFunctions, BrandDto brand, CarStatusDto carStatus, CarAddressDto address) implements Serializable {
        /**
         * DTO for {@link com.rentalcar.rentalcar.entity.AdditionalFunction}
         */
        public record AdditionalFunctionDto(Integer functionId, String functionName) implements Serializable {
        }

        /**
         * DTO for {@link com.rentalcar.rentalcar.entity.Brand}
         */
        public record BrandDto(Integer brandId, String brandName) implements Serializable {
        }

        /**
         * DTO for {@link com.rentalcar.rentalcar.entity.CarStatus}
         */
        public record CarStatusDto(Integer statusId, String name) implements Serializable {
        }

        /**
         * DTO for {@link com.rentalcar.rentalcar.entity.CarAddress}
         */
        public record CarAddressDto(Integer addressId, Integer provinceId, String province, Integer districtId, String district, Integer wardId, String ward, String street) implements Serializable {
        }
    }
}