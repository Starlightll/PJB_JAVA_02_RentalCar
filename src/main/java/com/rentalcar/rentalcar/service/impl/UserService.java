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
        return UserMapper.INSTANCE.UserToUserInfoResponse(user);
    }

    @Override
    public boolean updateUserInfo(UpdateUserInfoRequest updateUserInfoRequest) {
        User user = ValidateBusinessRequestUpdateUserInfo(updateUserInfoRequest);
        //Todo
        user.setFullName(updateUserInfoRequest.getFullName());

        try {
            userRepo.save(user);
            return true;
        } catch (Exception e) {
            return false;
        }
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
