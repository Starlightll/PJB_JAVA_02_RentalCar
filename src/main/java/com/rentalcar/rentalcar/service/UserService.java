package com.rentalcar.rentalcar.service;

import com.rentalcar.rentalcar.common.UserStatus;
import com.rentalcar.rentalcar.dto.UserDto;
import com.rentalcar.rentalcar.dto.UserInfoDto;
import com.rentalcar.rentalcar.entity.User;
import com.rentalcar.rentalcar.entity.UserRole;
import com.rentalcar.rentalcar.repository.UserRepo;
import com.rentalcar.rentalcar.repository.UserRoleRepo;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService implements IUserService {
    private final UserRepo userRepo;

    @Autowired
    private UserRoleRepo UserRoleRepo;

    @Override
    public void saveUser(UserInfoDto user, HttpSession session ) {
        User users = userRepo.getUserByEmail(user.getEmail());
        if(users != null){
            users.setFullName(user.getFullName());
            users.setDob(user.getDob());
            users.setPhone(user.getPhone());
            users.setNationalId(user.getNationalId());
            MultipartFile drivingLicenseFile = user.getDrivingLicense();
            if (drivingLicenseFile != null && !drivingLicenseFile.isEmpty()) {
                try {
                    // Define the directory where the file will be saved
                    String uploadDir = "uploads/DriveLicense/" + users.getId()+ "_" + users.getUsername() +"/"; // Update as needed
                    Files.createDirectories(Paths.get(uploadDir)); // Ensure directory exists

                    String fileName = drivingLicenseFile.getOriginalFilename();
                    Path filePath = Paths.get(uploadDir, fileName);

                    // Save the file
                    Files.write(filePath, drivingLicenseFile.getBytes());

                    // Set the relative path for storing in the database
                    users.setDrivingLicense(uploadDir + fileName);

                } catch (IOException e) {
                    e.printStackTrace();
                    // Optionally, log error or rethrow as custom exception
                }
            }
            users.setCity(user.getCity());
            users.setDistrict(user.getDistrict());
            users.setWard(user.getWard());
            users.setStreet(user.getStreet());
             userRepo.save(users);
             session.setAttribute("user", users);


        }

    }

    @Override
    public boolean checkPhone(String phone) {
            return userRepo.existsByPhone(phone);
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userRepo.getAllUsers();
        return users.stream().map(this::convertUserToUserDto).collect(Collectors.toList());
    }

    @Override
    public User addUser(User user) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        return userRepo.save(user);
    }

    @Override
    public void setUserRole(User user, Integer roleId) {
        Long userId = user.getId();
        UserRole userRole = new UserRole();
        userRole.setUserId(userId);
        userRole.setRoleId(roleId);
        UserRoleRepo.save(userRole);
    }

    @Override
    public void setUserStatus(User user, UserStatus status) {
        user.setStatus(status);
        userRepo.save(user);
    }

    public boolean checkEmail(String email) {
        return userRepo.getUserByEmail(email) != null;
    }


    //Convert user to userDto
    public UserDto convertUserToUserDto(User user){
        return UserDto.builder()
                .userId(user.getId())
                .fullName(user.getFullName())
                .username(user.getUsername())
                .dob(user.getDob())
                .phone(user.getPhone())
                .email(user.getEmail())
                .role(user.getRoles().isEmpty()?"THE FUCK, WHERE IS MY ROLE ?":user.getRoles().get(0).getRoleName())
                .status(user.getStatus().getStatus())
                .build();
    }



}
