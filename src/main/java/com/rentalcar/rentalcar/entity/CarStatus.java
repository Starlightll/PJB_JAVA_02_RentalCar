package com.rentalcar.rentalcar.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class CarStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CarStatusId")
    private Integer statusId;
    private String name;

    @OneToMany(mappedBy = "carStatus")
    private Set<Car> cars = new HashSet<>();

}

