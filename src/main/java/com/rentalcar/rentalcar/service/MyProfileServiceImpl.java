package com.rentalcar.rentalcar.service;

import com.rentalcar.rentalcar.dto.MyProfileDto;
import com.rentalcar.rentalcar.entity.User;
import com.rentalcar.rentalcar.exception.UserException;
import com.rentalcar.rentalcar.repository.UserRepo;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;



@Service
public class MyProfileServiceImpl implements MyProfileService {

    @Autowired UserRepo userRepo;


    @Override
    public void changePassword(MyProfileDto myProfileDto, HttpSession session) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        User user = (User) session.getAttribute("user");
        if(user == null) {
            throw new UserException("User not found");
        }

        if(!passwordEncoder.matches(myProfileDto.getOldPassword(), user.getPassword())) {
            throw new UserException("Old password is incorrect");
        }

        if(!myProfileDto.getNewPassword().equalsIgnoreCase(myProfileDto.getConfirmPassword())){
            throw new UserException("Passwords do not match");
        }

        // Save pass success
        String encodedNewPassword = passwordEncoder.encode(myProfileDto.getNewPassword());
        user.setPassword(encodedNewPassword);
        userRepo.save(user);
    }
}
