package org.example.ecommerce.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserDTO(
        Long id,
        String username,
        String email,
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        String password,
        String role
) {
    public UserDTO(String username, String email, String password, String role
    ) {
        this(null, username, email, password, role);
    }

    public UserDTO(Long id, String username, String email, String role
    ) {
        this(id, username, email, null, role);
    }
}
