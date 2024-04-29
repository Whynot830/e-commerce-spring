package org.example.ecommerce.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.ecommerce.dto.UserDTO;
import org.example.ecommerce.exception.NoAuthenticationException;
import org.example.ecommerce.mapper.UserDTOMapper;
import org.example.ecommerce.model.Order;
import org.example.ecommerce.model.Role;
import org.example.ecommerce.model.User;
import org.example.ecommerce.repository.OrderRepository;
import org.example.ecommerce.repository.UserRepository;
import org.example.ecommerce.util.StrChecker;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.example.ecommerce.model.OrderStatus.CART;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepo;
    private final OrderRepository orderRepo;
    private final PasswordEncoder passwordEncoder;
    private final UserDTOMapper mapper;

    public User getUserFromAuthentication(Authentication authentication) {
        if (authentication == null)
            throw new NoAuthenticationException();
        return (User) authentication.getPrincipal();
    }

    public UserDTO create(UserDTO userDTO) {
        Stream<String> values = Stream.of(userDTO.username(),
                userDTO.email(), userDTO.password(), userDTO.role());
        if (values.anyMatch(StrChecker::isNullOrBlank))
            throw new IllegalArgumentException("Some of properties are null or blank");

        var user = User.builder()
                .username(userDTO.username())
                .email(userDTO.email())
                .password(passwordEncoder.encode(userDTO.password()))
                .role(Role.valueOf(userDTO.role()))
                .build();

        user = userRepo.save(user);

        orderRepo.save(Order.builder()
                .status(CART)
                .total(BigDecimal.valueOf(0))
                .items(Collections.emptyList())
                .user(user)
                .build());

        return mapper.apply(user);
    }

    public List<UserDTO> readAll() {
        return userRepo.findAll().stream().map(mapper).toList();
    }

    public UserDTO read(Long id) {
        return userRepo.findById(id).map(mapper).orElseThrow(EntityNotFoundException::new);
    }

    public UserDTO read(Authentication authentication) {
        var user = getUserFromAuthentication(authentication);
        return mapper.apply(user);
    }

    public void delete(Long id) {
        var user = userRepo.findById(id).orElseThrow(EntityNotFoundException::new);
        userRepo.delete(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepo.findByEmail(email);
    }
}
