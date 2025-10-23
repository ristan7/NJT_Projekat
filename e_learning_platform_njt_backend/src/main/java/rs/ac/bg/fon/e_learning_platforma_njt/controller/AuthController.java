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

import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.Operation;

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

    // ===== NOVO: ADMIN-only promena role preko postojećeg UserDto =====
    @PatchMapping("/admin/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Promeni rolu korisniku (ADMIN-only) — kroz UserDto šalje se roleId")
    public ResponseEntity<Void> changeUserRole(
            @PathVariable Long id,
            @RequestBody UserDto body, // ⬅️ bez @Valid da ne pali validaciju za email/ime
            Authentication auth
    ) throws Exception {

        Long roleId = body != null ? body.getRoleId() : null;
        if (roleId == null) {
            // pošto ne koristimo @Valid, sami validiramo da je roleId prisutan
            throw new IllegalArgumentException("roleId is required");
        }

        // (opciono) možeš ovde ograničiti na {1,2,3} ako želiš striktno:
        // if (roleId < 1 || roleId > 3) throw new IllegalArgumentException("Invalid roleId");
        authService.changeUserRole(id, roleId, auth.getName());
        return ResponseEntity.noContent().build();
    }
}
