package com.rentalcar.rentalcar.dto;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MyBookingDto {
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
    private String str_numberOfDays;
    private Integer paymentMethod;
    private Double basePrice;
    private Double deposit;
    private String bookingStatus;


    private String frontImage;
    private String backImage;
    private String leftImage;
    private String rightImage;
    private Integer carId;
    //=======================================================
//    private String fullname;
//    @DateTimeFormat(pattern = "yyyy-MM-dd")
//    private LocalDate dob;
//    private String email;
//    private String nationalId;
//    private String phone;
//    private String drivingLicense;
//    private BigDecimal wallet;
//    private String city;
//    private String district;
//    private String ward;
//    private String street;


//    private String driverFullName;
//    @DateTimeFormat(pattern = "yyyy-MM-dd")
//    private LocalDate driverDob;
//    private String driverEmail;
//    private String driverNationalId;
//    private String driverPhone;
//    private String driverDrivingLicense;
//    private String driverCity;
//    private String driverDistrict;
//    private String driverWard;
//    private String driverStreet;
// ================================================
    private String Address;
    private Long driverId;
    private Integer bookingStatusId;
    private BigDecimal carOwnerWallet;
    private Long carOwnerId;
    private String lateTime;
    private double fineLateTime;
    private double returnDeposit;
    private double totalMoney;
    private double salaryDriver;
    private double totalPayment;



    //Update Booking
    public MyBookingDto(Long id, String carname, LocalDateTime startDate, LocalDateTime endDate, String driverInfo, LocalDateTime actualEndDate, double totalPrice, Long userId, String numberOfDays, Integer paymentMethod, double basePrice,
                        double deposit, String bookingStatus, String frontImage, String backImage, String leftImage, String rightImage, Integer carId, Long  driverId, String lateTime, double fineLateTime , double returnDeposit, double totalMoney,
                        double salaryDriver, double totalPayment) {
        this.bookingId = id;
        this.carname = carname;
        this.startDate = startDate;
        this.endDate = endDate;
        this.driverInfo = driverInfo;
        this.actualEndDate = actualEndDate;
        this.totalPrice = totalPrice;
        this.userId = userId;
        this.str_numberOfDays = numberOfDays;
        this.paymentMethod = paymentMethod;
        this.basePrice = basePrice;
        this.deposit = deposit;
        this.bookingStatus = bookingStatus;
        this.frontImage = frontImage;
        this.backImage = backImage;
        this.leftImage = leftImage;
        this.rightImage = rightImage;
        this.carId = carId;
        this.driverId = driverId;
        this.lateTime = lateTime;
        this.fineLateTime = fineLateTime;
        this.returnDeposit = returnDeposit;
        this.totalMoney = totalMoney;
        this.salaryDriver = salaryDriver;
        this.totalPayment = totalPayment;
    }


    //MyBookings
    public MyBookingDto(Long id, String carname, LocalDateTime startDate, LocalDateTime endDate, String driverInfo, LocalDateTime actualEndDate, double totalPrice, Long userId, String numberOfDays, Integer paymentMethod, double basePrice,
                        double deposit, String bookingStatus, String frontImage, String backImage, String leftImage, String rightImage, Integer carId,Long  driverId,
                        String lateTime, double fineLateTime , double returnDeposit, double totalMoney, double totalPayment) {
        this.bookingId = id;
        this.carname = carname;
        this.startDate = startDate;
        this.endDate = endDate;
        this.driverInfo = driverInfo;
        this.actualEndDate = actualEndDate;
        this.totalPrice = totalPrice;
        this.userId = userId;
        this.str_numberOfDays = numberOfDays;
        this.paymentMethod = paymentMethod;
        this.basePrice = basePrice;
        this.deposit = deposit;
        this.bookingStatus = bookingStatus;
        this.frontImage = frontImage;
        this.backImage = backImage;
        this.leftImage = leftImage;
        this.rightImage = rightImage;
        this.carId = carId;
        this.driverId = driverId;
        this.lateTime = lateTime;
        this.fineLateTime = fineLateTime;
        this.returnDeposit = returnDeposit;
        this.totalMoney = totalMoney;
        this.totalPayment = totalPayment;

    }

    public MyBookingDto(Long id,  LocalDateTime startDate, LocalDateTime endDate, String driverInfo, LocalDateTime actualEndDate, double totalPrice, Long userId,Integer bookingStatusId,
                        Integer paymentMethod, Long driverId, double basePrice, Double deposit, BigDecimal carOwnerWallet, Long carOwnerId,
                        String carname, Integer carId) {
        this.bookingId = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.driverInfo = driverInfo;
        this.actualEndDate = actualEndDate;
        this.totalPrice = totalPrice;
        this.userId = userId;
        this.paymentMethod = paymentMethod;
        this.basePrice = basePrice;
        this.bookingStatusId = bookingStatusId;
        this.driverId = driverId;
        this.deposit = deposit;
        this.carOwnerWallet = carOwnerWallet;
        this.carOwnerId = carOwnerId;
        this.carname = carname;
        this.carId = carId;
    }


}
