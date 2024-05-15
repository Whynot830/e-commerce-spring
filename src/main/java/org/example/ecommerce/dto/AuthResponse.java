package org.example.ecommerce.dto;

public record AuthResponse(
        UserDTO userDTO,
        String accessToken,
        String refreshToken
) {
}
