package com.rentalcar.rentalcar.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class AdditionalFunction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AdditionalFunctionId")
    private Integer functionId;
    @Column(name = "functionName")
    private String functionName;


    @ManyToMany(mappedBy = "additionalFunctions")
    private Set<Car> cars = new HashSet<>();
}
