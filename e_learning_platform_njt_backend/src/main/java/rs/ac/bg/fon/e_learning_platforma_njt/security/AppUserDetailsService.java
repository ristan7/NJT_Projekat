// rs/ac/bg/fon/e_learning_platforma_njt/security/AppUserDetailsService.java
package rs.ac.bg.fon.e_learning_platforma_njt.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.impl.User;
import rs.ac.bg.fon.e_learning_platforma_njt.repository.impl.UserRepository;

import java.util.List;

@Service
public class AppUserDetailsService implements UserDetailsService {

    private final UserRepository users;

    public AppUserDetailsService(UserRepository users) {
        this.users = users;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u = users.findByUsernameWithRole(username);
        if (u == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        String raw = (u.getRole() != null && u.getRole().getRoleName() != null)
                ? u.getRole().getRoleName() : "STUDENT";

        String norm = raw.trim().toUpperCase();
        if (!norm.startsWith("ROLE_")) {
            norm = "ROLE_" + norm;
        }

        var authorities = List.of(new SimpleGrantedAuthority(norm));

        return org.springframework.security.core.userdetails.User
                .withUsername(u.getUsername())
                .password(u.getPasswordHash())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}
