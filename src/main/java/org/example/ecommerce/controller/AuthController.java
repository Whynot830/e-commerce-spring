package org.example.ecommerce.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.ecommerce.dto.AuthResponse;
import org.example.ecommerce.dto.UserDTO;
import org.example.ecommerce.service.AuthService;
import org.example.ecommerce.util.CookieUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

import static org.example.ecommerce.util.CookieUtils.createJwtCookie;
import static org.springframework.http.HttpHeaders.SET_COOKIE;

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
    public ResponseEntity<UserDTO> login(
            @RequestBody UserDTO userDTO
    ) {
        AuthResponse res = authService.login(userDTO);
        var accessJwtCookie = createJwtCookie("access_token", res.accessToken());
        var refreshJwtCookie = createJwtCookie("refresh_token", res.refreshToken());
        return ResponseEntity
                .ok()
                .header(
                        SET_COOKIE, accessJwtCookie.toString(), refreshJwtCookie.toString()
                )
                .body(res.userDTO());
    }

    @PostMapping("/refresh")
    public ResponseEntity<UserDTO> refresh(
            HttpServletRequest request
    ) {
        final String refreshJwt = CookieUtils.extractTokenFromCookie(request, "refresh_token");
        AuthResponse res = authService.refresh(refreshJwt);
        var accessJwtCookie = createJwtCookie("access_token", res.accessToken());
        var refreshJwtCookie = createJwtCookie("refresh_token", res.refreshToken());
        return ResponseEntity
                .ok()
                .header(
                        SET_COOKIE, accessJwtCookie.toString(), refreshJwtCookie.toString()
                )
                .body(res.userDTO());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        var emptyAccessJwtCookie = createJwtCookie("access_token", "", 0);
        var emptyRefreshJwtCookie = createJwtCookie("refresh_token", "", 0);
        return ResponseEntity
                .noContent()
                .header(
                        SET_COOKIE, emptyAccessJwtCookie.toString(), emptyRefreshJwtCookie.toString()
                )
                .build();
    }
}
