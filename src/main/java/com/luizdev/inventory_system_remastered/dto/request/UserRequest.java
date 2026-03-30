package com.luizdev.inventory_system_remastered.dto.request;

import com.luizdev.inventory_system_remastered.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserRequest(
        @NotBlank
        String name,

        @NotBlank
        @Email
        String email,

        @NotBlank
        @Size(min = 6)
        String password,

        @NotNull
        UserRole role
) {
}
