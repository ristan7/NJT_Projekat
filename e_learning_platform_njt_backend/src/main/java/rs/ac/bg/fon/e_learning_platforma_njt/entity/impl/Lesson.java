package rs.ac.bg.fon.e_learning_platforma_njt.entity.impl;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.MyEntity;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.lookup.LessonType;

@Entity
@Table(name = "lesson",
        indexes = {
            @Index(name = "ix_lesson_course", columnList = "course_id"),
            @Index(name = "ix_lesson_type", columnList = "lesson_type_id"),
            @Index(name = "ix_lesson_order", columnList = "course_id,lesson_order_index")
        })
public class Lesson implements MyEntity{

    /* ===================== Polja ===================== */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lesson_id")
    private Long lessonId;

    @NotBlank(message = "Lesson title is required.")
    @Size(max = 150, message = "Lesson title can be at most 150 characters.")
    @Column(name = "lesson_title", nullable = false, length = 150)
    private String lessonTitle;

    @Size(max = 300, message = "Lesson summary can be at most 300 characters.")
    @Column(name = "lesson_summary", length = 300)
    private String lessonSummary;

    @Positive(message = "Lesson order index must be positive.")
    @Column(name = "lesson_order_index", nullable = false)
    private Integer lessonOrderIndex;

    @Column(name = "lesson_available", nullable = false)
    private boolean lessonAvailable = false;

    @PastOrPresent
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PastOrPresent
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /* ===================== Relacije ===================== */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lesson_type_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_lesson_type"))
    private LessonType lessonType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_lesson_course"))
    private Course course;

    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("materialOrderIndex ASC")
    private List<Material> materials = new ArrayList<>();

    /* ===================== Lifecycle hook-ovi za datume ===================== */
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
    public Lesson() {
    }

    public Lesson(Long lessonId) {
        this.lessonId = lessonId;
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

    public boolean isLessonAvailable() {
        return lessonAvailable;
    }

    public void setLessonAvailable(boolean lessonAvailable) {
        this.lessonAvailable = lessonAvailable;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public LessonType getLessonType() {
        return lessonType;
    }

    public void setLessonType(LessonType lessonType) {
        this.lessonType = lessonType;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public List<Material> getMaterials() {
        return materials;
    }

    public void setMaterials(List<Material> materials) {
        this.materials = materials;
    }

    /* ===================== Helper metode ===================== */
    public void addMaterial(Material material) {
        if (material == null) {
            return;
        }
        materials.add(material);
        material.setLesson(this);
    }

    public void removeMaterial(Material material) {
        if (material == null) {
            return;
        }
        materials.remove(material);
        material.setLesson(null);
    }

    /* ===================== equals, hashCode, toString ===================== */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Lesson)) {
            return false;
        }
        Lesson other = (Lesson) o;
        return Objects.equals(lessonId, other.lessonId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lessonId);
    }

    @Override
    public String toString() {
        return "Lesson{"
                + "lessonId=" + lessonId
                + ", lessonTitle='" + lessonTitle + '\''
                + ", lessonOrderIndex=" + lessonOrderIndex
                + ", lessonAvailable=" + lessonAvailable
                + ", lessonType=" + (lessonType != null ? lessonType.getLessonTypeName() : "null")
                + '}';
    }
}
