// src/main/java/.../mapper/impl/UserMapper.java
package rs.ac.bg.fon.e_learning_platforma_njt.mapper.impl;

import org.springframework.stereotype.Component;
import rs.ac.bg.fon.e_learning_platforma_njt.dto.impl.UserDto;
import rs.ac.bg.fon.e_learning_platforma_njt.dto.impl.lookups.RoleDto;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.impl.User;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.lookup.Role;

@Component
public class UserMapper {

    public UserDto toDto(User u) {
        if (u == null) {
            return null;
        }
        return new UserDto(
                u.getUserId(),
                u.getUsername(),
                u.getEmail(),
                u.getFirstName(),
                u.getLastName(),
                toRoleDto(u.getRole())
        );
    }

    public void updateFromRegister(User u, String firstName, String lastName) {
        u.setFirstName(firstName);
        u.setLastName(lastName);
    }

    private RoleDto toRoleDto(Role r) {
        if (r == null) {
            return null;
        }
        return new RoleDto(r.getRoleId(), r.getRoleName());
    }
    
}
