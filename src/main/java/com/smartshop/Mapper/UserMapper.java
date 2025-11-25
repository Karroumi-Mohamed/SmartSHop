package com.smartshop.Mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.smartshop.DTO.Request.UserCreateRequest;
import com.smartshop.DTO.Response.UserResponse;
import com.smartshop.Entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toResponse(User user);

    List<UserResponse> toResponseList(List<User> users);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(UserCreateRequest request);

}
