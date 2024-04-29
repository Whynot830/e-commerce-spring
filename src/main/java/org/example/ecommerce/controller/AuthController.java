package org.example.ecommerce.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.ecommerce.dto.UserDTO;
import org.example.ecommerce.service.AuthService;
import org.example.ecommerce.util.CookieUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.example.ecommerce.util.CookieUtils.createJwtCookie;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Auth API")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Void> register(
            @RequestBody UserDTO userDTO
    ) {
        authService.register(userDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(
            @RequestBody UserDTO userDTO,
            HttpServletResponse response
    ) {
        final String[] tokens = authService.login(userDTO);
        response.addCookie(createJwtCookie(tokens[0], "access_token"));
        response.addCookie(createJwtCookie(tokens[1], "refresh_token"));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<Void> refresh(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        final String refreshJwt = CookieUtils.extractTokenFromCookie(request, "refresh_token");
        final String[] tokens = authService.refresh(refreshJwt);
        response.addCookie(createJwtCookie(tokens[0], "access_token"));
        response.addCookie(createJwtCookie(tokens[1], "refresh_token"));
        return ResponseEntity.ok().build();
    }
}
