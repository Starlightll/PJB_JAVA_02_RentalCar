package com.rentalcar.rentalcar.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ForgotDto {
    @NotBlank(message = "Email cannot blank")
    @Email(message = "Valid email is required: ex@abc.xyz")
    private  String email;

    @Pattern(regexp = "(?=.*[0-9])(?=.*[!@#$%^&*()\\[\\]{}\\-_+=~`|:;\"'<>,./?])(?=.*[a-z])(?=.*[A-Z]).{8,}",
            message = "Password must contain at least 1 uppercase, 1 lowercase letter, 1 digit, and 1 special character.")
    private String password;

    @Pattern(regexp = "(?=.*[0-9])(?=.*[!@#$%^&*()\\[\\]{}\\-_+=~`|:;\"'<>,./?])(?=.*[a-z])(?=.*[A-Z]).{8,}",
            message = "Password must contain at least 1 uppercase, 1 lowercase letter, 1 digit, and 1 special character.")
    private String confirmPassword;

}
