package com.rentalcar.rentalcar.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class CarAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer addressId;
    private Integer provinceId;
    private String province;
    private Integer districtId;
    private String district;
    private Integer wardId;
    private String ward;
    private String street;

    @OneToOne
    @MapsId
    @JoinColumn(name = "carId")
    private Car car;

}
