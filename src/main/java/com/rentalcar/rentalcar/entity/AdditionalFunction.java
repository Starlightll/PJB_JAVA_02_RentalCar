package com.rentalcar.rentalcar.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
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
