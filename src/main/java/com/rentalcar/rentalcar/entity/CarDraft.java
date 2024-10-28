package com.rentalcar.rentalcar.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class CarDraft {

    @Id
    private Integer draftId;
    private Integer step;
    @Column(name = "name")
    private String carName;
    private String licensePlate;
    private LocalDate lastModified;
    private String model;
    private String color;
    @Column(name = "seatNo")
    private int seat;
    private int productionYear;
    private String transmission;
    @Column(name = "fuel")
    private String fuelType;
    private float mileage;
    private float fuelConsumption;
    private float basePrice;
    private float deposit;
    private String description;
    @Column(name = "termOfUse")
    private String terms;
    private float carPrice;
    @Column(name = "front")
    private String frontImage;
    @Column(name = "back")
    private String backImage;
    @Column(name = "[left]")
    private String leftImage;
    @Column(name = "[right]")
    private String rightImage;
    private String registration;
    private String certificate;
    private String insurance;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;
}
