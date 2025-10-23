package rs.ac.bg.fon.e_learning_platforma_njt.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import rs.ac.bg.fon.e_learning_platforma_njt.dto.impl.AuthResponse;
import rs.ac.bg.fon.e_learning_platforma_njt.dto.impl.LoginRequest;
import rs.ac.bg.fon.e_learning_platforma_njt.dto.impl.RegisterRequest;
import rs.ac.bg.fon.e_learning_platforma_njt.dto.impl.UserDto;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.impl.User;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.lookup.Role;
import rs.ac.bg.fon.e_learning_platforma_njt.mapper.impl.UserMapper;
import rs.ac.bg.fon.e_learning_platforma_njt.repository.impl.UserRepository;
import rs.ac.bg.fon.e_learning_platforma_njt.security.JwtService;

@Service
public class AuthService {

    private final AuthenticationManager authManager;
    private final JwtService jwt;
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final UserMapper userMapper;

    public AuthService(AuthenticationManager authManager,
            JwtService jwt,
            UserRepository users,
            PasswordEncoder encoder,
            UserMapper userMapper) {
        this.authManager = authManager;
        this.jwt = jwt;
        this.users = users;
        this.encoder = encoder;
        this.userMapper = userMapper;
    }

    /**
     * Registracija — početna rola je STUDENT (role_id = 1).
     */
    public UserDto register(RegisterRequest req) throws Exception {
        if (users.existsByUsername(req.getUsername())) {
            throw new Exception("Username already taken");
        }
        if (users.existsByEmail(req.getEmail())) {
            throw new Exception("Email already taken");
        }

        // Kreiraj korisnika
        User u = new User();
        u.setUsername(req.getUsername());
        u.setEmail(req.getEmail());
        u.setPasswordHash(encoder.encode(req.getPassword()));

        // Postavi referencu na STUDENT rolu po ID-ju (bez RoleRepository)
        Role studentRole = new Role();
        studentRole.setRoleId(1L); // 1 = STUDENT
        u.setRole(studentRole);

        // Dodatni podaci iz registracije
        userMapper.updateFromRegister(u, req.getFirstName(), req.getLastName());

        users.save(u);
        return userMapper.toDto(u);
    }

    /**
     * Prijava — generiše JWT token i vraća podatke o korisniku.
     */
    public AuthResponse login(LoginRequest req) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
        );

        UserDetails springUser = (UserDetails) auth.getPrincipal();
        User me = users.findByUsernameWithRole(req.getUsername());

        String roleName = (me != null && me.getRole() != null)
                ? me.getRole().getRoleName()
                : "STUDENT";

        String token = jwt.generate(springUser, Map.of("role", roleName));
        return new AuthResponse(token, userMapper.toDto(me));
    }

    /**
     * Vraća trenutno prijavljenog korisnika (na osnovu tokena).
     */
    public UserDto me(Authentication auth) throws Exception {
        if (auth == null || auth.getName() == null) {
            throw new Exception("Unauthenticated");
        }

        User me = users.findByUsernameWithRole(auth.getName());
        if (me == null) {
            throw new Exception("User not found");
        }

        return userMapper.toDto(me);
    }

    /**
     * Lista svih korisnika — koristi se za prikaz u admin delu / combobox.
     */
    public List<UserDto> listAllUsers() {
        return users.findAll()
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }
}
