package com.rentalcar.rentalcar.entity;

import lombok.*;
import org.apache.poi.hpsf.Decimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@NoArgsConstructor
@Entity
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String carId;
    private String carName;
    private String licensePlate;
    private String model;
    private String color;
    private int seat;
    private int productionYear;
    private String transmission;
    private String fuelType;
    private float mileage;
    private float fuelConsumption;
    private float basePrice;
    private float deposit;
    private String description;
    private String terms;
    private float carPrice;

}
