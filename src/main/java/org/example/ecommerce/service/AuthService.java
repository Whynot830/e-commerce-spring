package org.example.ecommerce.service;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.ecommerce.dto.AuthResponse;
import org.example.ecommerce.dto.UserDTO;
import org.example.ecommerce.exception.NoAuthenticationException;
import org.example.ecommerce.mapper.UserDTOMapper;
import org.example.ecommerce.model.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import static org.example.ecommerce.util.StrChecker.isNullOrBlank;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtService jwtService;
    private final AuthenticationManager authManager;
    private final UserService userService;
    private final UserDTOMapper mapper;

    private User getUserByJwt(String jwt) {
        try {
            String username = jwtService.getUsername(jwt);
            return userService.findByUsername(username).orElseThrow(EntityNotFoundException::new);
        } catch (ExpiredJwtException | EntityNotFoundException ignored) {
            throw new NoAuthenticationException();
        }
    }

    public void register(UserDTO userDTO) {
        UserDTO acceptedDTO = new UserDTO(userDTO.username(), userDTO.email(),
                userDTO.password(), "USER");
        userService.create(acceptedDTO);
    }

    public AuthResponse login(UserDTO userDTO) {
        String usernameOrEmail = isNullOrBlank(userDTO.username()) ? userDTO.email() : userDTO.username();
        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(usernameOrEmail, userDTO.password()));
        } catch (BadCredentialsException exception) {
            throw new NoAuthenticationException("Bad credentials");
        }
        var user = userService
                .findByUsername(usernameOrEmail)
                .orElseGet(
                        () -> userService.findByEmail(usernameOrEmail)
                                .orElseThrow(NoAuthenticationException::new)
                );
        String accessJwt = jwtService.generateToken(user);
        String refreshJwt = jwtService.generateRefreshToken(user);
        return new AuthResponse(mapper.apply(user), accessJwt, refreshJwt);
    }

    public AuthResponse refresh(String refreshJwt) {
        try {
            var user = getUserByJwt(refreshJwt);
            var newAccessJwt = jwtService.generateToken(user);
            var newRefreshJwt = jwtService.generateRefreshToken(user);
            return new AuthResponse(mapper.apply(user), newAccessJwt, newRefreshJwt);
        } catch (IllegalArgumentException ex) {
            throw new NoAuthenticationException();
        }
    }
}
