package com.rentalcar.rentalcar.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;

    private String carname;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String driverInfo;
    private LocalDateTime actualEndDate;

    private Double totalPrice;
    private Long userId;
    private Integer numberOfDays;
    private Integer paymentMethod;
    private Double basePrice;
    private Double deposit;
    private String bookingStatus;


    private String frontImage;
    private String backImage;
    private String leftImage;
    private String rightImage;

    public BookingDto (Long id,String carname, LocalDateTime startDate, LocalDateTime endDate, String driverInfo, LocalDateTime actualEndDate, double totalPrice, Long userId, int numberOfDays,Integer paymentMethod, double basePrice,
                       double deposit,  String bookingStatus, String frontImage, String backImage, String leftImage, String rightImage) {
        this.bookingId = id;
        this.carname = carname;
        this.startDate = startDate;
        this.endDate = endDate;
        this.driverInfo = driverInfo;
        this.actualEndDate = actualEndDate;
        this.totalPrice = totalPrice;
        this.userId = userId;
        this.numberOfDays = numberOfDays;
        this.paymentMethod = paymentMethod;
        this.basePrice = basePrice;
        this.deposit = deposit;
        this.bookingStatus = bookingStatus;
        this.frontImage = frontImage;
        this.backImage = backImage;
        this.leftImage = leftImage;
        this.rightImage = rightImage;
    }


}
