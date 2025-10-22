// src/main/java/.../dto/impl/UserDto.java
package rs.ac.bg.fon.e_learning_platforma_njt.dto.impl;

import rs.ac.bg.fon.e_learning_platforma_njt.dto.Dto;
import rs.ac.bg.fon.e_learning_platforma_njt.dto.impl.lookups.RoleDto;

import jakarta.validation.constraints.*;

/**
 * DTO za entitet User — zadržava tvoju strukturu, samo dodate validacione anotacije.
 */
public class UserDto implements Dto {

    private Long id;

    @NotBlank(message = "Username is required.")
    @Size(max = 50, message = "Username can be at most 50 characters.")
    private String username;

    @NotBlank(message = "Email is required.")
    @Email(message = "Email must be valid.")
    @Size(max = 100, message = "Email can be at most 100 characters.")
    private String email;

    @NotBlank(message = "First name is required.")
    @Size(max = 50, message = "First name can be at most 50 characters.")
    private String firstName;

    @NotBlank(message = "Last name is required.")
    @Size(max = 50, message = "Last name can be at most 50 characters.")
    private String lastName;

    @NotNull(message = "Role is required.")
    private RoleDto role;

    public UserDto() {
    }

    public UserDto(Long id, String username, String email, String firstName, String lastName, RoleDto role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public RoleDto getRole() {
        return role;
    }

    public void setRole(RoleDto role) {
        this.role = role;
    }
}
