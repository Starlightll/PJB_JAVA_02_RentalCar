package com.rentalcar.rentalcar.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {

    private Integer bookingId;
    private String location;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime pickUpDate;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime returnDate;
    private String driverInfo;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime actualEndDate;
    private Double totalPrice;
    private Integer numberOfDays;
    private Integer paymentMethod;
     private Double basePrice;
    private String deposit;
    @JsonProperty("isCheck")
    private Boolean isCheck;

    private Integer carID;

    private String rentFullName;

    private LocalDate rentBookPickDate;
    private String rentMail;
    private String rentNationalId;
    private String rentPhone;
    private String rentDrivingLicense;
    private BigDecimal wallet;
    private String rentProvince;
    private String rentDistrict;
    private String rentWard;
    private String rentStreet;
    private int step;
    private int selectedPaymentMethod;
    private Integer selectedUserId;


}
