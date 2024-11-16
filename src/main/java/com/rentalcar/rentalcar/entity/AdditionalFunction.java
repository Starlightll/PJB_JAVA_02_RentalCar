package com.rentalcar.rentalcar.entity;


import lombok.*;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.List;
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
