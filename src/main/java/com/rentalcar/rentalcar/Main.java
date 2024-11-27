package com.rentalcar.rentalcar;

import com.rentalcar.rentalcar.entity.Car;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class Main {
    public static void main(String[] args) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode("123");
        System.out.println(encodedPassword);
    }
}
