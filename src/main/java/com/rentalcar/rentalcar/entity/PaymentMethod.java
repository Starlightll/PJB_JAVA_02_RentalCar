package com.rentalcar.rentalcar.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "PaymentMethod")
public class PaymentMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PaymentMethodId")
    private Long paymentMethodId; // ID của phương thức thanh toán

    @Column(name = "name", nullable = false, length = 50)
    private String name;
}
