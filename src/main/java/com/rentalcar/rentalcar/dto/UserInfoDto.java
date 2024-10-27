package com.rentalcar.rentalcar.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
public class UserInfoDto {

        private Long id;

        @NotBlank(message = "Username is required")
        private String username;

        @DateTimeFormat(pattern = "yyyy-MM-dd")
        @Past(message = "Date of birth must be in the past")
        private LocalDate dob;

        @NotBlank(message = "Email is required")
        @Email(message = "Please provide a valid email address")
        private String email;

        @NotBlank(message = "National ID is required")
        private String nationalId;

        @NotBlank(message = "Phone number is required")
        @Pattern(regexp = "^\\+?[0-9. ()-]{7,25}$", message = "Phone number is invalid")
        private String phone;

        private String drivingLicense;

        @DecimalMin(value = "0.0", inclusive = true, message = "Wallet balance must be positive")
        private BigDecimal wallet;

        private String city;
        private String district;
        private String ward;
        private String street;

        @NotBlank(message = "Full name is required")
        private String fullName;
}
