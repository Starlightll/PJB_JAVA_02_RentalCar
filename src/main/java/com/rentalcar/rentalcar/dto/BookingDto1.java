package com.rentalcar.rentalcar.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * DTO for {@link com.rentalcar.rentalcar.entity.Booking}
 */
public record BookingDto1(Long bookingId, LocalDateTime startDate, LocalDateTime endDate, String driverInfo,
                          LocalDateTime actualEndDate, Double deposit, Double totalPrice, Date lastModified,
                          UserDto1 user, BookingStatusDto bookingStatus,
                          PaymentMethodDto paymentMethod) implements Serializable {
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
}