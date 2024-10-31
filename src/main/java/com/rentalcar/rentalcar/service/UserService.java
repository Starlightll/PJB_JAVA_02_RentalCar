package com.rentalcar.rentalcar.service;

import com.rentalcar.rentalcar.dto.UserInfoDto;
import com.rentalcar.rentalcar.entity.User;
import com.rentalcar.rentalcar.repository.UserRepo;


import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

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
            users.setDrivingLicense(user.getDrivingLicense());
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
