package com.rentalcar.rentalcar.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
public class UpdateUserInfoRequest {
    private Long id;
    private String fullName;
    private LocalDate dob;
    private String phone;
    private String email;
    private String nationalId;

    private String drivingLicense;
    private String city;
    private String district;
    private String ward;
    private String street;


}
