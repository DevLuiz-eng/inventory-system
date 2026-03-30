package com.luizdev.inventory_system_remastered.mapper;

import com.luizdev.inventory_system_remastered.dto.request.UserRequest;
import com.luizdev.inventory_system_remastered.dto.response.UserResponse;
import com.luizdev.inventory_system_remastered.entity.User;

public class UserMapper {

    public static User toEntity(UserRequest request) {
        User user = new User();
        user.setPassword(request.password());
        user.setRole(request.role());
        user.setEmail(request.email());
        user.setName(request.name());

        return user;
    }

    public static UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getActive()
        );
    }
}
