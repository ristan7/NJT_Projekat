package rs.ac.bg.fon.e_learning_platforma_njt.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.HttpStatus;

import rs.ac.bg.fon.e_learning_platforma_njt.dto.impl.AuthResponse;
import rs.ac.bg.fon.e_learning_platforma_njt.dto.impl.LoginRequest;
import rs.ac.bg.fon.e_learning_platforma_njt.dto.impl.RegisterRequest;
import rs.ac.bg.fon.e_learning_platforma_njt.dto.impl.UserDto;
import rs.ac.bg.fon.e_learning_platforma_njt.service.AuthService;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody RegisterRequest req) throws Exception {
        return ResponseEntity.ok(authService.register(req));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        // JWT je stateless → logout je na klijentu (obriši token)
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> me(Authentication auth) throws Exception {
        if (auth == null || auth.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401
        }
        return ResponseEntity.ok(authService.me(auth));
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> allUsers() {
        return ResponseEntity.ok(authService.listAllUsers());
    }
}
