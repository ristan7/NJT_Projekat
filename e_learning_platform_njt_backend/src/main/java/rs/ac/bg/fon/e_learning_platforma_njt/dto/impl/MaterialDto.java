package rs.ac.bg.fon.e_learning_platforma_njt.dto.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import rs.ac.bg.fon.e_learning_platforma_njt.validation.OneOfLong;

/**
 * DTO klasa za entitet Material. Koristi se pri kreiranju, izmeni i prikazu materijala. Datumi su read-only polja koja backend automatski popunjava.
 */
public class MaterialDto implements Serializable {

    /* ===================== Polja ===================== */
    @Positive(message = "Material ID must be a positive number.")
    private Long materialId;

    @NotBlank(message = "Material title is required.")
    @Size(max = 150, message = "Material title can be at most 150 characters.")
    private String materialTitle;

    @NotNull(message = "Material order index is required.")
    @Positive(message = "Material order index must be positive.")
    private Integer materialOrderIndex;

    @Size(max = 10000, message = "Content can be at most 10,000 characters.")
    private String content;

    @Size(max = 1000, message = "Resource URL can be at most 1000 characters.")
    private String resourceUrl;

    @NotNull(message = "Material type ID is required.")
    @Positive(message = "Material type ID must be positive.")
    @OneOfLong(
            value = {1, 2, 3, 4, 5},
            message = "Material type ID must be one of predefined values: "
            + "1 (PDF), 2 (IMAGE), 3 (LINK), 4 (PRESENTATION), 5 (VIDEO)."
    )
    private Long materialTypeId;

    @NotNull(message = "Lesson ID is required.")
    @Positive(message = "Lesson ID must be a positive number.")
    private Long lessonId;

    /* ===================== Read-only polja ===================== */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime updatedAt;

    /* ===================== Konstruktori ===================== */
    public MaterialDto() {
    }

    public MaterialDto(Long materialId, String materialTitle, Integer materialOrderIndex,
            String content, String resourceUrl,
            Long materialTypeId, Long lessonId,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.materialId = materialId;
        this.materialTitle = materialTitle;
        this.materialOrderIndex = materialOrderIndex;
        this.content = content;
        this.resourceUrl = resourceUrl;
        this.materialTypeId = materialTypeId;
        this.lessonId = lessonId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /* ===================== Getteri i Setteri ===================== */
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

    /* ===================== toString ===================== */
    @Override
    public String toString() {
        return "MaterialDto{"
                + "materialId=" + materialId
                + ", materialTitle='" + materialTitle + '\''
                + ", materialOrderIndex=" + materialOrderIndex
                + ", resourceUrl='" + resourceUrl + '\''
                + ", materialTypeId=" + materialTypeId
                + ", lessonId=" + lessonId
                + ", createdAt=" + createdAt
                + ", updatedAt=" + updatedAt
                + '}';
    }
}
