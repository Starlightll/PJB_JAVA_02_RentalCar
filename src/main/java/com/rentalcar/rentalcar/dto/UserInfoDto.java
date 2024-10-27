package com.rentalcar.rentalcar.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
@Data
@Builder
@AllArgsConstructor
public class UserInfoDto {


        private Long id;
        private String username;

        @DateTimeFormat(pattern = "yyyy-MM-dd")
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


}
