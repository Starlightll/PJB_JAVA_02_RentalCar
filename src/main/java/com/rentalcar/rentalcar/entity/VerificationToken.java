package com.rentalcar.rentalcar.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data

public class VerificationToken {
    private static final int EXPIRATION = 60*24;  // Thời hạn token là 24 giờ

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tokenId")
    private Long id;  // Khóa chính

    @Column(name = "tokenCode")
    private String token;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userId", nullable = false, unique = true)
    private User user;

    private LocalDateTime  expiryDate;

    public VerificationToken() {
    }

    public VerificationToken(String token, User user) {
        this.token = token;
        this.user = user;
        this.expiryDate = calculateExpiryDate(EXPIRATION);
    }

    private LocalDateTime  calculateExpiryDate(int expiryTimeInMinutes) {
        //Trả về time hiện tại
        return LocalDateTime.now().plusMinutes(expiryTimeInMinutes);
    }

    // Getters và setters
}
