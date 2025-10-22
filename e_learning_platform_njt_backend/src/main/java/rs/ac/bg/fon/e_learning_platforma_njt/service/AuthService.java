package rs.ac.bg.fon.e_learning_platforma_njt.service;

import java.util.List;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

import rs.ac.bg.fon.e_learning_platforma_njt.dto.impl.AuthResponse;
import rs.ac.bg.fon.e_learning_platforma_njt.dto.impl.LoginRequest;
import rs.ac.bg.fon.e_learning_platforma_njt.dto.impl.RegisterRequest;
import rs.ac.bg.fon.e_learning_platforma_njt.dto.impl.UserDto;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.impl.User;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.lookup.Role;
import rs.ac.bg.fon.e_learning_platforma_njt.mapper.impl.UserMapper;
import rs.ac.bg.fon.e_learning_platforma_njt.repository.lookups.RoleRepository;
import rs.ac.bg.fon.e_learning_platforma_njt.repository.impl.UserRepository;
import rs.ac.bg.fon.e_learning_platforma_njt.security.JwtService;

@Service
public class AuthService {

    private final AuthenticationManager authManager;
    private final JwtService jwt;
    private final UserRepository users;
    private final RoleRepository roles;
    private final PasswordEncoder encoder;
    private final UserMapper userMapper;

    public AuthService(AuthenticationManager authManager,
            JwtService jwt,
            UserRepository users,
            RoleRepository roles,
            PasswordEncoder encoder,
            UserMapper userMapper) {
        this.authManager = authManager;
        this.jwt = jwt;
        this.users = users;
        this.roles = roles;
        this.encoder = encoder;
        this.userMapper = userMapper;
    }

    /**
     * Registracija — inicijalna rola je obavezno STUDENT.
     */
    public UserDto register(RegisterRequest req) throws Exception {
        if (users.existsByUsername(req.getUsername())) {
            throw new Exception("Username already taken");
        }
        if (users.existsByEmail(req.getEmail())) {
            throw new Exception("Email already taken");
        }

        Role student = roles.findByRoleName("STUDENT");
        if (student == null) {
            throw new Exception("Default role STUDENT not configured");
        }

        User u = new User();
        u.setUsername(req.getUsername());
        u.setEmail(req.getEmail());
        u.setPasswordHash(encoder.encode(req.getPassword()));
        u.setRole(student);

        // NEW:
        userMapper.updateFromRegister(u, req.getFirstName(), req.getLastName());

        users.save(u);
        return userMapper.toDto(u);
    }

    public AuthResponse login(LoginRequest req) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
        );

        UserDetails springUser = (UserDetails) auth.getPrincipal();

        User me = users.findByUsernameWithRole(req.getUsername()); // ⬅️
        String roleName = (me != null && me.getRole() != null) ? me.getRole().getRoleName() : "STUDENT";

        String token = jwt.generate(springUser, Map.of("role", roleName));
        return new AuthResponse(token, userMapper.toDto(me));
    }

    public UserDto me(Authentication auth) throws Exception {
        if (auth == null || auth.getName() == null) {
            throw new Exception("Unauthenticated");
        }
        User me = users.findByUsernameWithRole(auth.getName()); // ⬅️
        if (me == null) {
            throw new Exception("User not found");
        }
        return userMapper.toDto(me);
    }

    // NEW: za AddNotification combobox
    public List<UserDto> listAllUsers() {
        return users.findAll().stream().map(userMapper::toDto).collect(Collectors.toList());
    }

}
