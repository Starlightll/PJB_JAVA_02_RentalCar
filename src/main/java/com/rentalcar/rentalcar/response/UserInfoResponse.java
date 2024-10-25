package com.rentalcar.rentalcar.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class UserInfoResponse {
    private Long id;
    private String username;
    private LocalDate dob;
    private String email;
    private String nationalId;
    private String phone;
    private String drivingLicense;
    private BigDecimal wallet;
    private String city;
    private String district;
    private String ward;
    private String street;
    private String fullName;
    private boolean enabled;
}
