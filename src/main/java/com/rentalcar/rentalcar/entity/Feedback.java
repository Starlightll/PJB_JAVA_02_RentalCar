package com.rentalcar.rentalcar.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Feedback {

    @Id
    @Column(name = "FeedbackId")
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;
    private int rating;
    private String content;
    private Date dateTime;
    
}
