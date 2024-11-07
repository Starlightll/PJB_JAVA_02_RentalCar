package com.rentalcar.rentalcar.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
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

    @ManyToOne
    @JoinColumn(name = "bookingId", nullable = false)
    private Booking booking;
}
