// src/main/java/.../mapper/impl/UserMapper.java
package rs.ac.bg.fon.e_learning_platforma_njt.mapper.impl;

import org.springframework.stereotype.Component;
import rs.ac.bg.fon.e_learning_platforma_njt.dto.impl.UserDto;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.impl.User;
import rs.ac.bg.fon.e_learning_platforma_njt.mapper.DtoEntityMapper;

@Component
public class UserMapper implements DtoEntityMapper<UserDto, User> {

    @Override
    public UserDto toDto(User e) {
        if (e == null) {
            return null;
        }
        // Koristi settere da ne zavisimo od starog konstruktora sa RoleDto
        UserDto dto = new UserDto();
        dto.setUserId(e.getUserId());
        // Ako UserDto ima username polje, mapiramo ga; ako nema, slobodno obriši narednu liniju
        try {
            // Ova linija je bezbedna ako UserDto ima setUsername; u suprotnom je obriši
            UserDto.class.getMethod("setUsername", String.class);
            dto.getClass().getMethod("setUsername", String.class).invoke(dto, e.getUsername());
        } catch (Exception ignore) {
            // UserDto nema username -> preskačemo
        }

        dto.setEmail(e.getEmail());
        dto.setFirstName(e.getFirstName());
        dto.setLastName(e.getLastName());

        if (e.getRole() != null) {
            dto.setRoleId(e.getRole().getRoleId());
            // roleName je opciono, ali korisno za UI
            try {
                UserDto.class.getMethod("setRoleName", String.class);
                dto.setRoleName(e.getRole().getRoleName());
            } catch (Exception ignore) {
                // Ako UserDto nema roleName polje, samo punimo roleId
            }
        }

        return dto;
    }

    @Override
    public User toEntity(UserDto t) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public void updateFromRegister(User u, String firstName, String lastName) {
        u.setFirstName(firstName);
        u.setLastName(lastName);
    }

    @Override
    public void apply(UserDto t, User e) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
