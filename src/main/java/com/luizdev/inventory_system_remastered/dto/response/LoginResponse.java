package com.luizdev.inventory_system_remastered.dto.response;

import com.luizdev.inventory_system_remastered.enums.UserRole;

public record LoginResponse(
        String token,
        String name,
        String email,
        UserRole role
) {}