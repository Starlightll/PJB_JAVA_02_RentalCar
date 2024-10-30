package com.rentalcar.rentalcar.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class CarDraft {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer draftId;
    private Integer step;
    @Column(name = "name")
    private String carName;
    private String licensePlate;
    private Date lastModified;
    private String model;
    private String color;
    @Column(name = "seatNo")
    private Integer seat;
    private Integer productionYear;
    private String transmission;
    @Column(name = "fuel")
    private String fuelType;
    private Float mileage;
    private Float fuelConsumption;
    private String additionalFunction;
    private String province;
    private String district;
    private String ward;
    @Column(name = "street")
    private String home;
    private Float basePrice;
    private Float deposit;
    private String description;
    @Column(name = "termOfUse")
    private String terms;
    private Float carPrice;
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

    @ManyToOne
    @JoinColumn(name = "brandId")
    private Brand brand;
}
