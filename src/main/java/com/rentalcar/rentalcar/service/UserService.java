package com.rentalcar.rentalcar.service;

import com.rentalcar.rentalcar.dto.UserInfoDto;
import com.rentalcar.rentalcar.entity.User;
import com.rentalcar.rentalcar.repository.UserRepo;


import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@AllArgsConstructor
public class UserService implements IUserService {
    private final UserRepo userRepo;



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


//
//        private String code;
//        private String name;
//
//        // Getters and setters
//        public String getCode() { return code; }
//        public void setCode(String code) { this.code = code; }
//
//        public String getName() { return name; }
//        public void setName(String name) { this.name = name; }
//    }


}
