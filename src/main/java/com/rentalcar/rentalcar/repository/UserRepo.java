package com.rentalcar.rentalcar.repository;


import com.rentalcar.rentalcar.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepo extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.email = :email")
    User getUserByEmail(@Param("email") String email);

    @Query(value = "SELECT u.userId, u.fullName, u.dob, u.phone FROM Users u JOIN UserRole ur ON ur.userId = u.userId JOIN Role r ON r.RoleId = ur.roleId WHERE r.RoleId = 4 AND u.status = 'ACTIVATED';", nativeQuery = true)
    List<Object[]> getAllDriverAvailable();


    @Query(value = "SELECT\n" +
            "    u.userId, \n" +
            "    u.fullName, \n" +
            "    u.dob,\n" +
            "    u.phone\n" +
            "FROM \n" +
            "    [Users] u\n" +
            "JOIN \n" +
            "    [dbo].[UserRole] ur ON ur.userId = u.userId\n" +
            "JOIN \n" +
            "    [dbo].[Role] r ON r.RoleId = ur.roleId\n" +
            "WHERE\n" +
            "    r.RoleId = 4\n" +
            "    AND u.status = 'ACTIVATED'\n" +
            "\n" +
            "UNION\n" +
            "\n" +
            "SELECT\n" +
            "    u.userId, \n" +
            "    u.fullName, \n" +
            "    u.dob,\n" +
            "    u.phone\n" +
            "FROM \n" +
            "    [Users] u\n" +
            "JOIN \n" +
            "    [dbo].[UserRole] ur ON ur.userId = u.userId\n" +
            "JOIN \n" +
            "    [dbo].[Role] r ON r.RoleId = ur.roleId\n" +
            "JOIN \n" +
            "    [dbo].[Booking] b ON b.driverId = u.userId\n" +
            "WHERE\n" +
            "    r.RoleId = 4\n" +
            "    AND u.status = 'RENTED'\n" +
            "    AND b.bookingId = :bookingId; \n", nativeQuery = true)
    List<Object[]> getAllDriver(@Param("bookingId") Integer bookingId);

    boolean existsByNationalId(String nationalId);

    boolean existsByPhone(String phone);

    User getUserById(Long id);

    @Query("SELECT u FROM User u WHERE u.status != 'DELETED'")
    List<User> getAllUsers();

    //Select first User from Users where username = :username order by userId asc
    @Query(value = "SELECT TOP 1 * FROM Users WHERE username = :username ORDER BY userId ASC", nativeQuery = true)
    User getUserByUsername(@Param("username") String username);
}
