package rs.ac.bg.fon.e_learning_platforma_njt.dto.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import rs.ac.bg.fon.e_learning_platforma_njt.dto.Dto;
import rs.ac.bg.fon.e_learning_platforma_njt.validation.OneOfLong;

/**
 * DTO klasa za entitet Lesson. Koristi se za kreiranje, izmenu i prikaz lekcija. Datumi su read-only polja koja backend automatski popunjava.
 */
public class LessonDto implements Dto, Serializable {

    /* ===================== Polja ===================== */
    @Positive(message = "Lesson ID must be positive.")
    private Long lessonId;

    @NotBlank(message = "Lesson title is required.")
    @Size(max = 150, message = "Lesson title can be at most 150 characters.")
    private String lessonTitle;

    @Size(max = 300, message = "Lesson summary can be at most 300 characters.")
    private String lessonSummary;

    @NotNull(message = "Lesson order index is required.")
    @Positive(message = "Lesson order index must be positive.")
    private Integer lessonOrderIndex;

    @NotNull(message = "Lesson type ID is required.")
    @Positive(message = "Lesson type ID must be positive.")
    @OneOfLong(
            value = {1, 2, 3, 4},
            message = "Lesson type ID must be one of predefined values: "
            + "1 (VIDEO), 2 (ARTICLE), 3 (QUIZ), 4 (ASSIGNMENT)."
    )
    private Long lessonTypeId;

    @NotNull(message = "Course ID is required.")
    @Positive(message = "Course ID must be positive.")
    private Long courseId;

    @NotNull(message = "Lesson availability flag is required.")
    private Boolean lessonAvailable;

    /* ===================== Read-only polja ===================== */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime updatedAt;

    /* ===================== Konstruktori ===================== */
    public LessonDto() {
    }

    public LessonDto(Long lessonId, String lessonTitle, String lessonSummary,
            Integer lessonOrderIndex, Long lessonTypeId, Long courseId,
            Boolean lessonAvailable, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.lessonId = lessonId;
        this.lessonTitle = lessonTitle;
        this.lessonSummary = lessonSummary;
        this.lessonOrderIndex = lessonOrderIndex;
        this.lessonTypeId = lessonTypeId;
        this.courseId = courseId;
        this.lessonAvailable = lessonAvailable;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /* ===================== Getteri i Setteri ===================== */
    public Long getLessonId() {
        return lessonId;
    }

    public void setLessonId(Long lessonId) {
        this.lessonId = lessonId;
    }

    public String getLessonTitle() {
        return lessonTitle;
    }

    public void setLessonTitle(String lessonTitle) {
        this.lessonTitle = lessonTitle;
    }

    public String getLessonSummary() {
        return lessonSummary;
    }

    public void setLessonSummary(String lessonSummary) {
        this.lessonSummary = lessonSummary;
    }

    public Integer getLessonOrderIndex() {
        return lessonOrderIndex;
    }

    public void setLessonOrderIndex(Integer lessonOrderIndex) {
        this.lessonOrderIndex = lessonOrderIndex;
    }

    public Long getLessonTypeId() {
        return lessonTypeId;
    }

    public void setLessonTypeId(Long lessonTypeId) {
        this.lessonTypeId = lessonTypeId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Boolean getLessonAvailable() {
        return lessonAvailable;
    }

    public void setLessonAvailable(Boolean lessonAvailable) {
        this.lessonAvailable = lessonAvailable;
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

    /* ===================== toString ===================== */
    @Override
    public String toString() {
        return "LessonDto{"
                + "lessonId=" + lessonId
                + ", lessonTitle='" + lessonTitle + '\''
                + ", lessonOrderIndex=" + lessonOrderIndex
                + ", lessonTypeId=" + lessonTypeId
                + ", courseId=" + courseId
                + ", lessonAvailable=" + lessonAvailable
                + ", createdAt=" + createdAt
                + ", updatedAt=" + updatedAt
                + '}';
    }
}
