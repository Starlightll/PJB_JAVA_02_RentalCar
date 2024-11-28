package com.rentalcar.rentalcar.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rentalcar.rentalcar.repository.CarDraftRepository;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
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
    private Integer seat;
    @Column(name = "productionYear")
    private Integer productionYear;
    @Column(name = "transmission")
    private String transmission;
    @Column(name = "fuel")
    private String fuelType;
    @Column(name = "mileage")
    private Double mileage;
    @Column(name = "fuelConsumption")
    private Double fuelConsumption;
    @Column(name = "basePrice")
    private Double basePrice;
    @Column(name = "deposit")
    private Double deposit;
    @Column(name = "description")
    private String description;
    @Column(name = "termOfUse")
    private String terms;
    @Column(name = "carPrice")
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
    private Date lastModified;

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
    @JsonIgnore
    private Set<AdditionalFunction> additionalFunctions = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "brandId")
    @JsonIgnore
    private Brand brand;

    @ManyToOne
    @JoinColumn(name = "statusId")
    @JsonIgnore
    private CarStatus carStatus;

    @OneToOne (cascade = CascadeType.ALL)
    @JoinColumn(name = "carId", referencedColumnName = "carId")
    @JsonIgnore
    private CarAddress address;

    @ManyToOne
    @JoinColumn(name = "userId")
    @JsonBackReference
    private User user;

}
