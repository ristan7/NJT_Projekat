package rs.ac.bg.fon.e_learning_platforma_njt.dto.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import rs.ac.bg.fon.e_learning_platforma_njt.dto.Dto;
import rs.ac.bg.fon.e_learning_platforma_njt.validation.OneOfLong;

public class LessonDto implements Dto {

    @Positive(message = "Lesson ID must be positive.")
    private Long lessonId;

    @NotBlank(message = "Lesson title is required.")
    @Size(max = 150, message = "Lesson title can be at most 150 characters.")
    private String lessonTitle;

    @Size(max = 300, message = "Lesson summary can be at most 300 characters.")
    private String lessonSummary;

    @NotNull(message = "Lesson order is required.")
    @Positive(message = "Lesson order must be positive.")
    private Integer lessonOrderIndex;

    /**
     * Draft/Publish prekidač
     */
    private boolean lessonAvailable;

    /**
     * Ako je true i lekcija je available, student može da je gleda i bez enrolementa.
     */
    private boolean freePreview;

    @NotNull(message = "Lesson type ID is required.")
    @Positive(message = "Lesson type ID must be positive.")
    @OneOfLong(
            value = {1, 2, 3, 4},
            message = "Lesson type must be one of: 1 (VIDEO), 2 (ARTICLE), 3 (QUIZ), 4 (ASSIGNMENT)."
    )
    private Long lessonTypeId;

    @NotNull(message = "Course ID is required.")
    @Positive(message = "Course ID must be positive.")
    private Long courseId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime updatedAt;

    public LessonDto() {
    }

    // Getteri/Setteri
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

    public boolean isLessonAvailable() {
        return lessonAvailable;
    }

    public void setLessonAvailable(boolean lessonAvailable) {
        this.lessonAvailable = lessonAvailable;
    }

    public boolean isFreePreview() {
        return freePreview;
    }

    public void setFreePreview(boolean freePreview) {
        this.freePreview = freePreview;
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
}
