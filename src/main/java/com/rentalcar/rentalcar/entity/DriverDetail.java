package com.rentalcar.rentalcar.entity;


import com.rentalcar.rentalcar.common.UserStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;


@Data
@Entity
public class DriverDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "driverId")
    private Long id;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dob;

    private String email;
    private String nationalId;
    private String phone;
    private String drivingLicense;
    private String city;
    private String district;
    private String ward;
    private String street;
    private String fullName;

    @ManyToOne
    @JoinColumn(name = "bookingId")
    private Booking booking;
}
