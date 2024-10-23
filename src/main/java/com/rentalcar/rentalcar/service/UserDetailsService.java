package com.rentalcar.rentalcar.service;


import com.rentalcar.rentalcar.entity.User;

public interface UserDetailsService {
    public User loadUserByEmail(String email);
}
