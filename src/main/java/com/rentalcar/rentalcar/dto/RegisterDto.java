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
public class RegisterDto {

    @NotBlank(message = "User Name cannot blank")
    private String username;
    @NotBlank(message = "Email cannot blank")
    @Email(message = "Valid email is required: ex@abc.xyz")
    private  String email;
    @NotBlank(message = "Phone number cannot be blank")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number format") // Ví dụ cho số điện thoại
    private String phoneNumber;// Thêm trường số điện thoại
    @Size(min=8, max=20, message = "Password length must between 8 and 20 characters")
    private String password;
    @Size(min=8, max=20, message = "Password length must between 8 and 20 characters")
    private String confirmPassword;
    private int role;
}
