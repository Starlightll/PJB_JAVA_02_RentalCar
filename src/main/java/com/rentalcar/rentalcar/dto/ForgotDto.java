package com.rentalcar.rentalcar.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ForgotDto {
    @NotBlank(message = "Email cannot blank")
    @Email(message = "Please enter a valid email address", regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
    private  String email;

    @Pattern(regexp = "(?=.*[0-9])(?=.*[!@#$%^&*()\\[\\]{}\\-_+=~`|:;\"'<>,./?])(?=.*[a-z])(?=.*[A-Z]).{8,}",
            message = "Password must contain at least 1 uppercase, 1 lowercase letter, 1 digit, and 1 special character and 8 characters.")
    private String password;

    @Pattern(regexp = "(?=.*[0-9])(?=.*[!@#$%^&*()\\[\\]{}\\-_+=~`|:;\"'<>,./?])(?=.*[a-z])(?=.*[A-Z]).{8,}",
            message = "Password must contain at least 1 uppercase, 1 lowercase letter, 1 digit, and 1 special character and 8 characters.")
    private String confirmPassword;

}
