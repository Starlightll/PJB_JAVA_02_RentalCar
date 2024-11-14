package com.rentalcar.rentalcar.repository;


import com.rentalcar.rentalcar.dto.UserDto;
import com.rentalcar.rentalcar.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepo extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.email = :email")
    public User getUserByEmail(@Param("email") String email);

    @Query(value = "  select u.[userId], u.fullName, u.dob from [Users] u \n" +
            "  Join [dbo].[UserRole] ur ON ur.userId = u.userId\n" +
            "  Join [dbo].[Role] r ON r.RoleId = ur.roleId\n" +
            "  where r.RoleId = 4 and u.statusDriverId =  1", nativeQuery = true)
     List<Object[]> getAllDriverAvailable();


    boolean existsByPhone(String phone);

    User getUserById(Long id);

    @Query("SELECT u FROM User u")
    public List<User> getAllUsers();

}
