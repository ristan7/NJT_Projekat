package rs.ac.bg.fon.e_learning_platforma_njt.dto.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import rs.ac.bg.fon.e_learning_platforma_njt.dto.Dto;
import rs.ac.bg.fon.e_learning_platforma_njt.validation.OneOfLong;

/**
 * DTO klasa za entitet Course. Koristi se za kreiranje, izmenu i prikaz kurseva. Datumi i lessonCount su read-only polja koja popunjava backend.
 */
public class CourseDto implements Dto {

    /* ===================== Polja za unos/izmenu ===================== */
    @Positive(message = "Course ID must be positive.")
    private Long courseId;

    @NotBlank(message = "Course title is required.")
    @Size(max = 150, message = "Course title can be at most 150 characters.")
    private String courseTitle;

    @NotBlank(message = "Course description is required.")
    @Size(max = 20000, message = "Course description can be at most 20,000 characters.")
    private String courseDescription;

    @NotNull(message = "Author ID is required.")
    @Positive(message = "Author ID must be positive.")
    private Long authorId;

    @NotNull(message = "Course level ID is required.")
    @Positive(message = "Course level ID must be positive.")
    @OneOfLong(
            value = {1, 2, 3},
            message = "Course level ID must be one of predefined values: "
            + "1 (BEGINNER), 2 (INTERMEDIATE), 3 (ADVANCED)."
    )
    private Long courseLevelId;

    @NotNull(message = "Course status ID is required.")
    @Positive(message = "Course status ID must be positive.")
    @OneOfLong(
            value = {1, 2, 3},
            message = "Course status ID must be one of predefined values: "
            + "1 (DRAFT), 2 (PUBLISHED), 3 (ARCHIVED)."
    )
    private Long courseStatusId;

    /* ===================== Read-only polja ===================== */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime updatedAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime publishedAt;

    // Za listing kurseva (broj lekcija bez otključavanja sadržaja)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer lessonCount;

    /* ===================== Konstruktori ===================== */
    public CourseDto() {
    }

    public CourseDto(Long courseId, String courseTitle, String courseDescription,
            Long authorId, Long courseLevelId, Long courseStatusId,
            LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime publishedAt,
            Integer lessonCount) {
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        this.courseDescription = courseDescription;
        this.authorId = authorId;
        this.courseLevelId = courseLevelId;
        this.courseStatusId = courseStatusId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.publishedAt = publishedAt;
        this.lessonCount = lessonCount;
    }

    /* ===================== Getteri i Setteri ===================== */
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

    public String getCourseDescription() {
        return courseDescription;
    }

    public void setCourseDescription(String courseDescription) {
        this.courseDescription = courseDescription;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public Long getCourseLevelId() {
        return courseLevelId;
    }

    public void setCourseLevelId(Long courseLevelId) {
        this.courseLevelId = courseLevelId;
    }

    public Long getCourseStatusId() {
        return courseStatusId;
    }

    public void setCourseStatusId(Long courseStatusId) {
        this.courseStatusId = courseStatusId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    public Integer getLessonCount() {
        return lessonCount;
    }

    public void setLessonCount(Integer lessonCount) {
        this.lessonCount = lessonCount;
    }

    /* ===================== toString ===================== */
    @Override
    public String toString() {
        return "CourseDto{"
                + "courseId=" + courseId
                + ", courseTitle='" + courseTitle + '\''
                + ", authorId=" + authorId
                + ", courseLevelId=" + courseLevelId
                + ", courseStatusId=" + courseStatusId
                + ", createdAt=" + createdAt
                + ", updatedAt=" + updatedAt
                + ", publishedAt=" + publishedAt
                + ", lessonCount=" + lessonCount
                + '}';
    }
}
