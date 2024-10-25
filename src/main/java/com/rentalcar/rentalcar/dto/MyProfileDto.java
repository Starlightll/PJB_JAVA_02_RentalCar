package com.rentalcar.rentalcar.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MyProfileDto {

    @Size(min=8, max=20, message = "Password length must between 8 and 20 characters")
    private String oldPassword;
    @Size(min=8, max=20, message = "Password length must between 8 and 20 characters")
    private String newPassword;
    @Size(min=8, max=20, message = "Password length must between 8 and 20 characters")
    private String confirmPassword;
}
