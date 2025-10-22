package rs.ac.bg.fon.e_learning_platforma_njt.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
        User u = users.findByUsernameWithRole(username); // ⬅️ ovo je ključno
        if (u == null) {
            throw new UsernameNotFoundException("User not found");
        }

        String roleName = (u.getRole() != null && u.getRole().getRoleName() != null)
                ? u.getRole().getRoleName()
                : "STUDENT";

        var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + roleName));
        return new org.springframework.security.core.userdetails.User(
                u.getUsername(),
                u.getPasswordHash(),
                authorities
        );
    }

}
