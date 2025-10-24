package rs.ac.bg.fon.e_learning_platforma_njt.dto.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import rs.ac.bg.fon.e_learning_platforma_njt.dto.Dto;
import rs.ac.bg.fon.e_learning_platforma_njt.validation.OneOfLong;

public class MaterialDto implements Dto {

    @Positive(message = "Material ID must be positive.")
    private Long materialId;

    @NotBlank(message = "Material title is required.")
    @Size(max = 150, message = "Material title can be at most 150 characters.")
    private String materialTitle;

    @NotNull(message = "Material order is required.")
    @Positive(message = "Material order must be positive.")
    private Integer materialOrderIndex;

    @Size(max = 100000, message = "Content is too long.")
    private String content;

    @Size(max = 1000, message = "Resource URL can be at most 1000 characters.")
    private String resourceUrl;

    @NotNull(message = "Material type ID is required.")
    @Positive(message = "Material type ID must be positive.")
    @OneOfLong(
            value = {1, 2, 3, 4, 5},
            message = "Material type must be one of: 1 (PDF), 2 (IMAGE), 3 (LINK), 4 (PRESENTATION), 5 (VIDEO)."
    )
    private Long materialTypeId;

    @NotNull(message = "Lesson ID is required.")
    @Positive(message = "Lesson ID must be positive.")
    private Long lessonId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime updatedAt;

    public MaterialDto() {
    }

    /* Getteri/Setteri */
    public Long getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Long materialId) {
        this.materialId = materialId;
    }

    public String getMaterialTitle() {
        return materialTitle;
    }

    public void setMaterialTitle(String materialTitle) {
        this.materialTitle = materialTitle;
    }

    public Integer getMaterialOrderIndex() {
        return materialOrderIndex;
    }

    public void setMaterialOrderIndex(Integer materialOrderIndex) {
        this.materialOrderIndex = materialOrderIndex;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getResourceUrl() {
        return resourceUrl;
    }

    public void setResourceUrl(String resourceUrl) {
        this.resourceUrl = resourceUrl;
    }

    public Long getMaterialTypeId() {
        return materialTypeId;
    }

    public void setMaterialTypeId(Long materialTypeId) {
        this.materialTypeId = materialTypeId;
    }

    public Long getLessonId() {
        return lessonId;
    }

    public void setLessonId(Long lessonId) {
        this.lessonId = lessonId;
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
