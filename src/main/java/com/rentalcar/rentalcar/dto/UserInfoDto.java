package com.rentalcar.rentalcar.dto;

import com.rentalcar.rentalcar.util.ValidAge;
import jakarta.validation.constraints.*;
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


        @NotNull(message = "Date of birth is required")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        @Past(message = "Date of birth must be in the past")
        @ValidAge
        private LocalDate dob;

        @NotBlank(message = "Email is required")
        @Email(message = "Please provide a valid email address")
        private String email;

        @NotBlank(message = "National ID is required")
        @Pattern(regexp = "^[0-9]+$", message = "National ID must contain only numbers")
        private String nationalId;

        @NotBlank(message = "Phone number is required")
        @Pattern(regexp = "^\\+?[0-9]{10}$", message = "Phone number must contain only numbers")
        private String phone;

        @NotBlank(message = "Driving License ID is required")
        private String drivingLicense;


        private String city;
        private String district;
        private String ward;
        @NotBlank(message = "Street is required")
        private String street;

        @NotBlank(message = "Full name is required")
        private String fullName;

}
