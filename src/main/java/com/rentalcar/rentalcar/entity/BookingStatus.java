package com.rentalcar.rentalcar.entity;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class BookingStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingStatusId;

    @Column(nullable = false, length = 50)
    private String name;

}
