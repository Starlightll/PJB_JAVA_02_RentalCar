package com.rentalcar.rentalcar.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Feedback {

    @Id
    @Column(name = "FeedbackId")
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;
    private int rating;
    private String content;

    private LocalDateTime dateTime;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bookingId", nullable = false,  unique = true)
    private Booking booking;
}
