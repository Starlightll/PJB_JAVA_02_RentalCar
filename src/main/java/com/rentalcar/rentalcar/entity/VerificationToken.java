package com.rentalcar.rentalcar.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.rentalcar.rentalcar.common.Constants;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tokenId")
    private Long id;  // Khóa chính

    @Column(name = "tokenCode")
    private String token;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userId", nullable = false, unique = true)
    @JsonBackReference
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
