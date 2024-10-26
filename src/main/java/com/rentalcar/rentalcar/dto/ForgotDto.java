package com.rentalcar.rentalcar.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ForgotDto {
    @NotBlank(message = "Email cannot blank")
    @Email(message = "Valid email is required: ex@abc.xyz")
    private  String email;

    @Size(min=8, max=20, message = "Password length must between 8 and 20 characters")
    private String password;
    @Size(min=8, max=20, message = "Password length must between 8 and 20 characters")
    private String confirmPassword;

}
