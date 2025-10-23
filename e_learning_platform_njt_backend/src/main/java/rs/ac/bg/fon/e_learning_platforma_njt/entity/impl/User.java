// src/main/java/rs/ac/bg/fon/e_learning_platforma_njt/entity/impl/User.java
package rs.ac.bg.fon.e_learning_platforma_njt.entity.impl;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.MyEntity;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.lookup.Role;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(
        name = "user",
        uniqueConstraints = {
            @UniqueConstraint(name = "uk_user_username", columnNames = "username"),
            @UniqueConstraint(name = "uk_user_email", columnNames = "email")
        }
)
public class User implements MyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @NotBlank
    @Size(max = 50)
    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @NotBlank
    @Email
    @Size(max = 120)
    @Column(name = "email", nullable = false, unique = true, length = 120)
    private String email;

    @NotBlank
    @Size(min = 8, max = 255)
    @Column(name = "password", nullable = false, length = 255)
    private String passwordHash;

    @Size(max = 80)
    @Column(name = "first_name", length = 80)
    private String firstName;

    @Size(max = 80)
    @Column(name = "last_name", length = 80)
    private String lastName;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(
            name = "role_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_user_role")
    )
    private Role role;

    /* ======= NOTIFICATIONS (postojeće) ======= */
    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @OrderBy("sentAt DESC")
    private List<Notification> notifications = new ArrayList<>();

    /* ======= NEW: kursevi čiji je autor ovaj korisnik ======= */
    @OneToMany(
            mappedBy = "author", // u Course: @ManyToOne User author
            cascade = CascadeType.ALL, // brisanje user-a briše i njegove kurseve
            orphanRemoval = true, // uklanjanje iz liste briše red u course
            fetch = FetchType.LAZY
    )
    @OrderBy("createdAt DESC")
    private List<Course> authoredCourses = new ArrayList<>();

    /* ======= konstruktori ======= */
    public User() {
    }

    public User(Long userId) {
        this.userId = userId;
    }

    /* ======= helper metode (Notifications) ======= */
    public void addNotification(Notification n) {
        if (n == null) {
            return;
        }
        notifications.add(n);
        n.setUser(this);
    }

    public void removeNotification(Notification n) {
        if (n == null) {
            return;
        }
        notifications.remove(n);
        n.setUser(null);
    }

    /* ======= helper metode (Courses) ======= */
    public void addAuthoredCourse(Course c) {
        if (c == null) {
            return;
        }
        authoredCourses.add(c);
        c.setAuthor(this);
    }

    public void removeAuthoredCourse(Course c) {
        if (c == null) {
            return;
        }
        authoredCourses.remove(c);
        c.setAuthor(null);
    }

    /* ======= get/set ======= */
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
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

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications.clear();
        if (notifications != null) {
            for (Notification n : notifications) {
                addNotification(n);
            }
        }
    }

    public List<Course> getAuthoredCourses() {
        return authoredCourses;
    }

    public void setAuthoredCourses(List<Course> courses) {
        this.authoredCourses.clear();
        if (courses != null) {
            for (Course c : courses) {
                addAuthoredCourse(c);
            }
        }
    }

    /* ======= equals/hashCode/toString ======= */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User u)) {
            return false;
        }
        return Objects.equals(email, u.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    @Override
    public String toString() {
        return "User: " + username + " (" + email + ")";
    }
}
