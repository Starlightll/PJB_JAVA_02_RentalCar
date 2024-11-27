package com.rentalcar.rentalcar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.rentalcar.rentalcar"})
@EnableScheduling
public class RentalCarApplication {
    public static void main(String[] args) {
        SpringApplication.run(RentalCarApplication.class, args);
    }

}
