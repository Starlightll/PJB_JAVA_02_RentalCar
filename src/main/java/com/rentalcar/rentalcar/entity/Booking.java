package com.rentalcar.rentalcar.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private String driverInfo;

    private LocalDateTime actualEndDate;

    private Double deposit;

    private Double totalPrice;

    private Date lastModified;

    @ManyToOne
    @JoinColumn(name = "driverId")
    private User driver; // Liên kết với entity User (là tài xế)

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user; // Liên kết với entity User


    @ManyToOne
    @JoinColumn(name = "bookingStatusId", nullable = false)
    private BookingStatus bookingStatus; // Liên kết với entity BookingStatus

    @ManyToOne
    @JoinColumn(name = "paymentMethodId", nullable = false)
    private PaymentMethod paymentMethod; // Liên kết với entity PaymentMethod

    @OneToMany(mappedBy = "booking")
    private Set<BookingCar> bookingCars; // Liên kết với entity BookingCar

    public Car getCar() {
        for (BookingCar bookingCar : bookingCars) {
            if (bookingCar.getBookingId().equals(this.bookingId)) {
                return bookingCar.getCar();
            }
        }
        return null;
    }

}
