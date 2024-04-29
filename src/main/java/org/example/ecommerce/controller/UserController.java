package org.example.ecommerce.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.ecommerce.dto.UserDTO;
import org.example.ecommerce.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "Users", description = "Users API")
public class UserController {
    private final UserService userService;

    @PostMapping()
    public ResponseEntity<UserDTO> create(@RequestBody UserDTO userDTO) {
        var createdUser = userService.create(userDTO);
        return ResponseEntity.created(null).body(createdUser);
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> readAll() {
        var users = userService.readAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> read(@PathVariable Long id) {
        var user = userService.read(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/current")
    public ResponseEntity<UserDTO> read(Authentication authentication) {
        var user = userService.read((authentication));
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
