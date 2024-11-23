package com.rentalcar.rentalcar.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "BookingCar")
@IdClass(BookingCar.BookingCarId.class)
public class BookingCar {
    @Id
    @Column(name = "carId")
    private Long carId;

    @Id
    @Column(name = "bookingId")
    private Long bookingId;


    @ManyToOne
    @JoinColumn(name = "carId", insertable = false, updatable = false)
    private Car car;

    @ManyToOne
    @JoinColumn(name = "bookingId", insertable = false, updatable = false)
    private Booking booking;


    public static class BookingCarId implements Serializable {
        private Long carId;
        private Long bookingId;
    }
}
