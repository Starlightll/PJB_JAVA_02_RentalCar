package com.rentalcar.rentalcar.entity;

import lombok.Data;

import jakarta.persistence.*;

@Data
@Entity
@Table(name = "Role")
public class Role {
    @Id
    @Column(name = "roleId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String roleName;
    public String toString() {
        return this.roleName;
    }
}