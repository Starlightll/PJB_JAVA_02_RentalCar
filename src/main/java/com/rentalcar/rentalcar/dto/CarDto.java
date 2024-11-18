package com.rentalcar.rentalcar.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter

@NoArgsConstructor
public class CarDto {
    private Long carId;
    private String name;
    private String licensePlate;
    private String model;
    private String color;
    private Integer seatNo;
    private Integer productionYear;
    private String transmission;
    private String fuel;
    private double mileage;
    private Double fuelConsumption;
    private Double basePrice;
    private Double deposit;
    private String description;
    private String termOfUse;
    private Double carPrice;
    private String front;
    private String back;
    private String left;
    private String right;
    private String registration;
    private String certificate;
    private String insurance;
    private Date lastModified;
    private Integer userId;
    private Integer brandId;
    private Integer statusId;
    private Double averageRating;  // Trường để lưu điểm trung bình đánh giá
    private Integer bookingStatusId;
    private String bookingStatusName;
    private Long bookingId;


    public CarDto(Long carId, String name, String licensePlate, String model, String color, Integer seatNo,
                  Integer productionYear, String transmission, String fuel, double mileage, double fuelConsumption,
                  double basePrice, double deposit, String description, String termOfUse, double carPrice,
                  String front, String back, String left, String right, String registration, String certificate,
                  String insurance, Date lastModified, Integer userId, Integer brandId, Integer statusId,
                  Double averageRating) {
        this.carId = carId;
        this.name = name;
        this.licensePlate = licensePlate;
        this.model = model;
        this.color = color;
        this.seatNo = seatNo;
        this.productionYear = productionYear;
        this.transmission = transmission;
        this.fuel = fuel;
        this.mileage = mileage;
        this.fuelConsumption = fuelConsumption;
        this.basePrice = basePrice;
        this.deposit = deposit;
        this.description = description;
        this.termOfUse = termOfUse;
        this.carPrice = carPrice;
        this.front = front;
        this.back = back;
        this.left = left;
        this.right = right;
        this.registration = registration;
        this.certificate = certificate;
        this.insurance = insurance;
        this.lastModified = lastModified;
        this.userId = userId;
        this.brandId = brandId;
        this.statusId = statusId;
        this.averageRating = averageRating;
    }

    public CarDto(Long carId, String name, String licensePlate, String model, String color, Integer seatNo,
                  Integer productionYear, String transmission, String fuel, double mileage, double fuelConsumption,
                  double basePrice, double deposit, String description, String termOfUse, double carPrice,
                  String front, String back, String left, String right, String registration, String certificate,
                  String insurance, Date lastModified, Integer userId, Integer brandId, Integer statusId,
                  Integer bookingStatusId, String bookingStatusName, Long bookingId) {
        this.carId = carId;
        this.name = name;
        this.licensePlate = licensePlate;
        this.model = model;
        this.color = color;
        this.seatNo = seatNo;
        this.productionYear = productionYear;
        this.transmission = transmission;
        this.fuel = fuel;
        this.mileage = mileage;
        this.fuelConsumption = fuelConsumption;
        this.basePrice = basePrice;
        this.deposit = deposit;
        this.description = description;
        this.termOfUse = termOfUse;
        this.carPrice = carPrice;
        this.front = front;
        this.back = back;
        this.left = left;
        this.right = right;
        this.registration = registration;
        this.certificate = certificate;
        this.insurance = insurance;
        this.lastModified = lastModified;
        this.userId = userId;
        this.brandId = brandId;
        this.statusId = statusId;
        this.bookingStatusId = bookingStatusId;
        this.bookingStatusName = bookingStatusName;
        this.bookingId = bookingId;
    }

}
