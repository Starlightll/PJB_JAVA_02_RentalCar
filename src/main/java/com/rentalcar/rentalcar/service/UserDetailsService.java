package com.rentalcar.rentalcar.service;


import com.rentalcar.rentalcar.entity.User;

public interface UserDetailsService {
    User loadUserByEmail(String email);
}
