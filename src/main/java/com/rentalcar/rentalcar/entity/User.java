package com.rentalcar.rentalcar.entity;

import com.rentalcar.rentalcar.common.UserStatus;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "Users")
public class User {
    @Id
    @Column(name = "userId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dob;
    @Column(unique = true)
    private String email;
    private String nationalId;
    @Column(unique = true)
    private String phone;
    private String drivingLicense;
    private BigDecimal wallet;
    private String password;
    private String city;
    private String district;
    private String ward;
    private String street;
    private String fullName;
    private boolean agreeTerms;
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private VerificationToken verificationToken; // Tham chiếu đến VerificationToken

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "UserRole",
            joinColumns = @JoinColumn(name = "userId"),
            inverseJoinColumns = @JoinColumn(name = "roleId")
    )
    private Set<Role> roles = new HashSet<>();



}