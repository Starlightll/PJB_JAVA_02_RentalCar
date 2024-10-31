package com.rentalcar.rentalcar.entity;

import lombok.*;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
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
    private double mileage;
    @Column(name = "fuelConsumption")
    private double fuelConsumption;
    @Column(name = "basePrice")
    private double basePrice;
    @Column(name = "deposit")
    private double deposit;
    @Column(name = "description")
    private String description;
    @Column(name = "termOfUse")
    private String terms;
    @Column(name = "carPrice")
    private double carPrice;
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

    @OneToOne (cascade = CascadeType.ALL)
    @JoinColumn(name = "carId", referencedColumnName = "carId")
    private CarAddress address;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

}
