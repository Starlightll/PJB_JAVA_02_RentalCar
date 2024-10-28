package com.rentalcar.rentalcar.entity;

import com.rentalcar.rentalcar.common.Constants;
import lombok.Data;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data

public class VerificationToken {

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
        this.expiryDate = calculateExpiryDate(Constants.EXPIRATION);
    }

    private LocalDateTime  calculateExpiryDate(int expiryTimeInMinutes) {
        //Trả về time hiện tại
        return LocalDateTime.now().plusMinutes(expiryTimeInMinutes);
    }

}
