package com.rentalcar.rentalcar.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rentalcar.rentalcar.common.UserStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Users")
public class User {
    @Id
    @Column(name = "userId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String avatar;
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
    private Double salaryDriver;
    private String descriptionDriver;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private VerificationToken verificationToken; // Tham chiếu đến VerificationToken

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "UserRole",
            joinColumns = @JoinColumn(name = "userId"),
            inverseJoinColumns = @JoinColumn(name = "roleId")
    )
    private List<Role> roles;

    @OneToMany
    @JoinColumn(name = "userId")
    @JsonIgnore
    private List<CarDraft> carDrafts = new ArrayList<>();

    @OneToMany
    @JoinColumn(name = "userId")
    @JsonIgnore
    private List<Car> cars = new ArrayList<>();

}