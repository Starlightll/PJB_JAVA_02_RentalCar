package com.rentalcar.rentalcar.entity;


import lombok.*;

import jakarta.persistence.*;

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

