package rs.ac.bg.fon.e_learning_platforma_njt.dto.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import rs.ac.bg.fon.e_learning_platforma_njt.dto.Dto;
import rs.ac.bg.fon.e_learning_platforma_njt.validation.OneOfLong;

/**
 * DTO za upis studenta na kurs (Enrollment).
 */
public class EnrollmentDto implements Dto {

    @Positive(message = "Enrollment ID must be positive.")
    private Long enrollmentId;

    /* Student */
    @NotNull(message = "Student ID is required.")
    @Positive(message = "Student ID must be positive.")
    private Long studentId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String studentUsername;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String studentEmail;

    /* Course */
    @NotNull(message = "Course ID is required.")
    @Positive(message = "Course ID must be positive.")
    private Long courseId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String courseTitle;

    /* Status */
    @NotNull(message = "Enrollment status ID is required.")
    @Positive(message = "Enrollment status ID must be positive.")
    @OneOfLong(
            value = {1, 2, 3, 4, 5},
            message = "Enrollment status must be one of: 1 (REQUESTED), 2 (ACTIVE), 3 (COMPLETED), 4 (CANCELLED), 5 (SUSPENDED)."
    )
    private Long statusId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String statusName;

    /* Datumi (read-only) */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime enrolledAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime lastAccessedAt;

    public EnrollmentDto() {
    }

    /* Getteri/Setteri */
    public Long getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(Long enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getStudentUsername() {
        return studentUsername;
    }

    public void setStudentUsername(String studentUsername) {
        this.studentUsername = studentUsername;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public Long getStatusId() {
        return statusId;
    }

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
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
}
