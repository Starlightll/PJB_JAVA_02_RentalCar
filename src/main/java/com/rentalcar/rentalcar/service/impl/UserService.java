package com.rentalcar.rentalcar.service.impl;

import com.rentalcar.rentalcar.entity.User;
import com.rentalcar.rentalcar.mapper.UserMapper;
import com.rentalcar.rentalcar.repository.UserRepo;
import com.rentalcar.rentalcar.request.UpdateUserInfoRequest;
import com.rentalcar.rentalcar.request.UserInfoRequest;
import com.rentalcar.rentalcar.response.UserInfoResponse;
import com.rentalcar.rentalcar.service.IUserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService implements IUserService {
    private final UserRepo userRepo;

    @Override
    public UserInfoResponse getUserInfo(UserInfoRequest userInfoRequest) {
        User user = ValidateBusinessRequestGetUserInfo(userInfoRequest);
   //     districts.addDistrict();

        return UserMapper.INSTANCE.UserToUserInfoResponse(user);
    }

    @Override
    public User saveUser(User user) {
         return userRepo.save(user);
    }
//    public class ProvinceDto {
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

    @Override
    public boolean updateUserInfo(UpdateUserInfoRequest updateUserInfoRequest) {
        User user = ValidateBusinessRequestUpdateUserInfo(updateUserInfoRequest);
        if (user != null) { // Ensure user exists
            user.setFullName(updateUserInfoRequest.getFullName());
            user.setDob(updateUserInfoRequest.getDob());
            user.setPhone(updateUserInfoRequest.getPhone());
            user.setEmail(updateUserInfoRequest.getEmail());
            user.setNationalId(updateUserInfoRequest.getNationalId());
            user.setDrivingLicense(updateUserInfoRequest.getDrivingLicense());
            user.setCity(updateUserInfoRequest.getCity());
            user.setDistrict(updateUserInfoRequest.getDistrict());
            user.setWard(updateUserInfoRequest.getWard());
            user.setStreet(updateUserInfoRequest.getStreet());

            try {
                userRepo.save(user);
                return true; // Successfully updated
            } catch (Exception e) {
                e.printStackTrace(); // Log the error (consider using a logger)
            }
        }
        return false; // Update failed
    }

    private User ValidateBusinessRequestGetUserInfo(UserInfoRequest userInfoRequest) {
        Optional<User> user = userRepo.findById(userInfoRequest.Id);
        if (user.isPresent()) {
            return user.get();
        } else {
            // Todo: Throw exception user not found
            return null;
        }
    }

    private User ValidateBusinessRequestUpdateUserInfo(UpdateUserInfoRequest updateUserInfoRequest) {
        Optional<User> user = userRepo.findById(updateUserInfoRequest.getId());
        if (user.isPresent()) {
            return user.get();
        } else {
            // Todo: Throw exception user not found
            return null;
        }
    }
}
