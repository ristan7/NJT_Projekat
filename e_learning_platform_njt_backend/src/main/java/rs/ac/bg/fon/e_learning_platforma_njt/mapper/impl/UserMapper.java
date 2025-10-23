// src/main/java/.../mapper/impl/UserMapper.java
package rs.ac.bg.fon.e_learning_platforma_njt.mapper.impl;

import org.springframework.stereotype.Component;
import rs.ac.bg.fon.e_learning_platforma_njt.dto.impl.UserDto;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.impl.User;

@Component
public class UserMapper {

    public UserDto toDto(User u) {
        if (u == null) {
            return null;
        }
        // Koristi settere da ne zavisimo od starog konstruktora sa RoleDto
        UserDto dto = new UserDto();
        dto.setUserId(u.getUserId());
        // Ako UserDto ima username polje, mapiramo ga; ako nema, slobodno obriši narednu liniju
        try {
            // Ova linija je bezbedna ako UserDto ima setUsername; u suprotnom je obriši
            UserDto.class.getMethod("setUsername", String.class);
            dto.getClass().getMethod("setUsername", String.class).invoke(dto, u.getUsername());
        } catch (Exception ignore) {
            // UserDto nema username -> preskačemo
        }

        dto.setEmail(u.getEmail());
        dto.setFirstName(u.getFirstName());
        dto.setLastName(u.getLastName());

        if (u.getRole() != null) {
            dto.setRoleId(u.getRole().getRoleId());
            // roleName je opciono, ali korisno za UI
            try {
                UserDto.class.getMethod("setRoleName", String.class);
                dto.setRoleName(u.getRole().getRoleName());
            } catch (Exception ignore) {
                // Ako UserDto nema roleName polje, samo punimo roleId
            }
        }

        return dto;
    }

    public void updateFromRegister(User u, String firstName, String lastName) {
        u.setFirstName(firstName);
        u.setLastName(lastName);
    }
}
