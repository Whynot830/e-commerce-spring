package org.example.ecommerce.config;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.ecommerce.service.JwtService;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import java.io.IOException;
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        if (request.getServletPath().contains("/api/auth")) {
            filterChain.doFilter(request, response);
            return;
        }
        final Cookie accessJwtCookie = WebUtils.getCookie(request, "access_token");

        if (accessJwtCookie == null) {
            filterChain.doFilter(request, response);
            return;
        }
        final String token = accessJwtCookie.getValue();
        final String username;
        try {
            username = jwtService.getUsername(token);
        } catch (ExpiredJwtException | IllegalArgumentException e) {
            filterChain.doFilter(request, response);
            return;
        }
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                var userDetails = this.userDetailsService.loadUserByUsername(username);
                var isTokenValid = jwtService.isTokenValid(token, userDetails);
                if (isTokenValid) {
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authenticationToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            } catch (UsernameNotFoundException | ExpiredJwtException e) {
                filterChain.doFilter(request, response);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
