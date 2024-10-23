package com.rentalcar.rentalcar.repository;

import com.rentalcar.rentalcar.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepo extends JpaRepository<UserRole, UserRole.UserRoleId> {
}
