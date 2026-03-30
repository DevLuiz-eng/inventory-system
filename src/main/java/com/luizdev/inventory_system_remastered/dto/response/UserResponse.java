package com.luizdev.inventory_system_remastered.dto.response;

import com.luizdev.inventory_system_remastered.enums.UserRole;

public record UserResponse(
        Long id,
        String name,
        String email,
        UserRole role,
        Boolean active
) {
}
