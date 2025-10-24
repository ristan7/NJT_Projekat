package rs.ac.bg.fon.e_learning_platforma_njt.security;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import rs.ac.bg.fon.e_learning_platforma_njt.service.RoleAccessService;

@Service
@Primary
public class AccessGuards implements RoleAccessService {

    @Override
    public boolean isAdmin(String roleName) {
        return equalsRole(roleName, "ADMIN");
    }

    @Override
    public boolean isTeacher(String roleName) {
        return equalsRole(roleName, "TEACHER");
    }

    @Override
    public boolean isStudent(String roleName) {
        return equalsRole(roleName, "STUDENT");
    }

    private boolean equalsRole(String roleName, String expected) {
        if (roleName == null) {
            return false;
        }
        String r = roleName.trim().toUpperCase();
        String e = expected.trim().toUpperCase();
        return r.equals(e) || r.equals("ROLE_" + e);
    }
}
