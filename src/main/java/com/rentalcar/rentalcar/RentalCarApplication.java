package com.rentalcar.rentalcar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.rentalcar.rentalcar"})
public class RentalCarApplication {
    public static void main(String[] args) {
        SpringApplication.run(RentalCarApplication.class, args);
    }

}
