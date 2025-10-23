package rs.ac.bg.fon.e_learning_platforma_njt.dto.impl;

import rs.ac.bg.fon.e_learning_platforma_njt.dto.Dto;
import rs.ac.bg.fon.e_learning_platforma_njt.validation.OneOfLong;
import jakarta.validation.constraints.*;

/**
 * DTO za korisnika — koristi isključivo roleId (bez RoleDto).
 */
public class UserDto implements Dto {

    @Positive(message = "User id must be a positive number.")
    private Long userId;

    @NotBlank(message = "Email is required.")
    @Email(message = "Email must be valid.")
    @Size(max = 120, message = "Email can be at most 120 characters.")
    private String email;

    @Size(max = 80, message = "First name can be at most 80 characters.")
    private String firstName;

    @Size(max = 80, message = "Last name can be at most 80 characters.")
    private String lastName;

    // U bazi: 1=STUDENT, 2=TEACHER, 3=ADMIN
    @OneOfLong(value = {1, 2, 3}, message = "Role must be 1 (Student), 2 (Teacher) or 3 (Admin).")
    @Positive(message = "Role id must be positive.")
    private Long roleId;

    // samo za read (zgodno za UI)
    private String roleName;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
