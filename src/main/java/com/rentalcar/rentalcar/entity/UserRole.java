package com.rentalcar.rentalcar.entity;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@Table(name = "UserRole")
@IdClass(UserRole.UserRoleId.class)  // Sử dụng @IdClass để chỉ định khóa chính phức hợp
public class UserRole {

    @Id
    @Column(name = "userId")
    private Long userId;

    @Id
    @Column(name = "roleId")
    private int roleId;

    @ManyToOne
    @JoinColumn(name = "userId", insertable = false, updatable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "roleId", insertable = false, updatable = false)
    private Role role;

    // Getters và Setters

    // Khóa chính phức hợp
    public static class UserRoleId implements Serializable {
        private Long userId;
        private int roleId;


    }
}
