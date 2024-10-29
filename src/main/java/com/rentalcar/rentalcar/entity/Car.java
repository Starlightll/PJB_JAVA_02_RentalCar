package com.rentalcar.rentalcar.entity;

import lombok.*;
import org.apache.poi.hpsf.Decimal;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer carId;
    @Column(name = "name")
    private String carName;
    @Column(name = "licensePlate")
    private String licensePlate;
    @Column(name = "model")
    private String model;
    @Column(name = "color")
    private String color;
    @Column(name = "seatNo")
    private int seat;
    @Column(name = "productionYear")
    private int productionYear;
    @Column(name = "transmission")
    private String transmission;
    @Column(name = "fuel")
    private String fuelType;
    @Column(name = "mileage")
    private float mileage;
    @Column(name = "fuelConsumption")
    private float fuelConsumption;
    private String province;
    private String district;
    private String ward;
    @Column(name = "street")
    private String home;
    @Column(name = "basePrice")
    private float basePrice;
    @Column(name = "deposit")
    private float deposit;
    @Column(name = "description")
    private String description;
    @Column(name = "termOfUse")
    private String terms;
    @Column(name = "carPrice")
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

    @ManyToMany
    @JoinTable(
            name = "CarAdditionalFunction",
            joinColumns = {
                    @JoinColumn(name = "carId")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "AdditionalFunctionId")
            }
    )
    private Set<AdditionalFunction> additionalFunctions = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "brandId")
    private Brand brand;

    @ManyToOne
    @JoinColumn(name = "statusId")
    private CarStatus carStatus;

}
