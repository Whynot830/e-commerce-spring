package org.example.ecommerce.mapper;

import org.example.ecommerce.dto.UserDTO;
import org.example.ecommerce.model.User;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class UserDTOMapper implements Function<User, UserDTO> {
    @Override
    public UserDTO apply(User user) {
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name());
    }
}
