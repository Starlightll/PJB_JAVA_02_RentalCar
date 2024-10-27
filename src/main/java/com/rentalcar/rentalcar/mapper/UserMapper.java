package com.rentalcar.rentalcar.mapper;

import com.rentalcar.rentalcar.entity.User;
import com.rentalcar.rentalcar.response.UserInfoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserInfoResponse UserToUserInfoResponse(User user);

}
