package rs.ac.bg.fon.e_learning_platforma_njt.entity.impl;

import jakarta.persistence.*;
import jakarta.validation.constraints.PastOrPresent;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.MyEntity;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.lookup.EnrollmentStatus;

@Entity
@Table(
        name = "enrollment",
        uniqueConstraints = {
            @UniqueConstraint(name = "uk_enrollment_student_course", columnNames = {"student_id", "course_id"})
        },
        indexes = {
            @Index(name = "ix_enrollment_student", columnList = "student_id"),
            @Index(name = "ix_enrollment_course", columnList = "course_id"),
            @Index(name = "ix_enrollment_status", columnList = "enrollment_status_id"),
            @Index(name = "ix_enrollment_enrolled_at", columnList = "enrolled_at")
        }
)
public class Enrollment implements Serializable, MyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "enrollment_id")
    private Long enrollmentId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false, foreignKey = @ForeignKey(name = "fk_enrollment_student"))
    private User student;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false, foreignKey = @ForeignKey(name = "fk_enrollment_course"))
    private Course course;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_status_id", nullable = false, foreignKey = @ForeignKey(name = "fk_enrollment_status"))
    private EnrollmentStatus status;

    @PastOrPresent
    @Column(name = "enrolled_at", nullable = false)
    private LocalDateTime enrolledAt;

    @PastOrPresent
    @Column(name = "last_accessed_at")
    private LocalDateTime lastAccessedAt;

    /* ===== Lifecycle ===== */
    @PrePersist
    private void onCreate() {
        if (enrolledAt == null) {
            enrolledAt = LocalDateTime.now();
        }
    }

    /* ===== Ctors ===== */
    public Enrollment() {
    }

    public Enrollment(User student, Course course, EnrollmentStatus status) {
        this.student = student;
        this.course = course;
        this.status = status;
    }

    /* ===== Get/Set ===== */
    public Long getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(Long enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public EnrollmentStatus getStatus() {
        return status;
    }

    public void setStatus(EnrollmentStatus status) {
        this.status = status;
    }

    public LocalDateTime getEnrolledAt() {
        return enrolledAt;
    }

    public void setEnrolledAt(LocalDateTime enrolledAt) {
        this.enrolledAt = enrolledAt;
    }

    public LocalDateTime getLastAccessedAt() {
        return lastAccessedAt;
    }

    public void setLastAccessedAt(LocalDateTime lastAccessedAt) {
        this.lastAccessedAt = lastAccessedAt;
    }

    /* ===== equals/hashCode/toString ===== */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Enrollment)) {
            return false;
        }
        Enrollment that = (Enrollment) o;
        return Objects.equals(enrollmentId, that.enrollmentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(enrollmentId);
    }

    @Override
    public String toString() {
        return "Enrollment{"
                + "id=" + enrollmentId
                + ", student=" + (student != null ? student.getEmail() : "null")
                + ", course=" + (course != null ? course.getCourseTitle() : "null")
                + ", status=" + (status != null ? status.getEnrollmentStatusName() : "null")
                + '}';
    }
}
