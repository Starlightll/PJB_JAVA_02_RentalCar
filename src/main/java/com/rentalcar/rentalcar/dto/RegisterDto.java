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
public class RegisterDto {

    @NotBlank(message = "User Name cannot blank.")
    private String username;
    @NotBlank(message = "Email cannot blank.")
    @Email(message = "Please enter a valid email address ", regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
    private  String email;
    @NotBlank(message = "Phone number cannot be blank.")
    @Pattern(regexp = "^\\+?[0-9]\\d{1,14}$", message = "Invalid phone number format.")
    private String phoneNumber;

    @Pattern(regexp = "(?=.*[0-9])(?=.*[!@#$%^&*()\\[\\]{}\\-_+=~`|:;\"'<>,./?])(?=.*[a-z])(?=.*[A-Z]).{8,}",
            message = "Password must contain at least 1 uppercase, 1 lowercase letter, 1 digit, and 1 special character and 8 characters.")
    private String password;

    @Pattern(regexp = "(?=.*[0-9])(?=.*[!@#$%^&*()\\[\\]{}\\-_+=~`|:;\"'<>,./?])(?=.*[a-z])(?=.*[A-Z]).{8,}",
            message = "Password must have (A-Z), (a-z), (0-9), a special char, and be 8+ chars.")
    private String confirmPassword;
    private int role;
    private boolean agreedTerms;
}
