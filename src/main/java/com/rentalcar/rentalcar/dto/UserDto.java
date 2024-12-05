package com.rentalcar.rentalcar.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {

    private Long userId;
    private String fullName;
    private String username;
    private String avatar;
    private LocalDate dob;
    private String email;
    private String phone;
    private String role;
    private String status;
}
