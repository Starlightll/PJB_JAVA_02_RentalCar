package com.rentalcar.rentalcar.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity

public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;

    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;

    private String driverInfo;

    @Temporal(TemporalType.TIMESTAMP)
    private Date actualEndDate;

    private Double totalPrice;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user; // Liên kết với entity User


}
