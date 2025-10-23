package rs.ac.bg.fon.e_learning_platforma_njt.entity.impl;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.Objects;

import rs.ac.bg.fon.e_learning_platforma_njt.entity.MyEntity;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.lookup.MaterialType;

@Entity
@Table(name = "material",
        indexes = {
            @Index(name = "ix_material_lesson", columnList = "lesson_id"),
            @Index(name = "ix_material_type", columnList = "material_type_id"),
            @Index(name = "ix_material_order", columnList = "lesson_id,material_order_index")
        })
public class Material implements MyEntity {

    /* ===================== Polja ===================== */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "material_id")
    private Long materialId;

    @NotBlank(message = "Material title is required.")
    @Size(max = 150, message = "Material title can be at most 150 characters.")
    @Column(name = "material_title", nullable = false, length = 150)
    private String materialTitle;

    @Positive(message = "Material order index must be positive.")
    @Column(name = "material_order_index", nullable = false)
    private Integer materialOrderIndex;

    @Column(name = "content", columnDefinition = "LONGTEXT")
    private String content; // Tekstualni sadržaj (npr. ARTICLE, opis)

    @Size(max = 1000, message = "Resource URL can be at most 1000 characters.")
    @Column(name = "resource_url", length = 1000)
    private String resourceUrl; // Link do PDF-a, videa, prezentacije itd.

    /* Audit datumi */
    @PastOrPresent
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PastOrPresent
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /* ===================== Relacije ===================== */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lesson_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_material_lesson"))
    private Lesson lesson;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "material_type_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_material_type"))
    private MaterialType materialType;

    /* ===================== Lifecycle hook-ovi (audit) ===================== */
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /* ===================== Konstruktori ===================== */
    public Material() {
    }

    public Material(Long materialId) {
        this.materialId = materialId;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Lesson getLesson() {
        return lesson;
    }

    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
    }

    public MaterialType getMaterialType() {
        return materialType;
    }

    public void setMaterialType(MaterialType materialType) {
        this.materialType = materialType;
    }

    /* ===================== equals, hashCode, toString ===================== */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Material)) {
            return false;
        }
        Material other = (Material) o;
        return Objects.equals(materialId, other.materialId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(materialId);
    }

    @Override
    public String toString() {
        return "Material{"
                + "materialId=" + materialId
                + ", materialTitle='" + materialTitle + '\''
                + ", materialOrderIndex=" + materialOrderIndex
                + ", resourceUrl='" + resourceUrl + '\''
                + ", materialType=" + (materialType != null ? materialType.getMaterialTypeName() : "null")
                + '}';
    }
}
