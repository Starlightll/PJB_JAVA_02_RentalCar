package com.rentalcar.rentalcar.mapper;

import com.rentalcar.rentalcar.dto.UserInfoDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.rentalcar.rentalcar.entity.User;
@Mapper

public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserInfoDto UserToUserInfoResponse(User user);
}
