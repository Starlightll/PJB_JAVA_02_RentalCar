package com.rentalcar.rentalcar.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
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
    private Double mileage;
    private Double fuelConsumption;
    private String additionalFunction;
    private String province;
    private String district;
    private String ward;
    @Column(name = "street")
    private String home;
    private Double basePrice;
    private Double deposit;
    private String description;
    @Column(name = "termOfUse")
    private String terms;
    private Double carPrice;
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
    @JsonBackReference
    private User user;

    @ManyToOne
    @JoinColumn(name = "brandId")
    private Brand brand;
}
