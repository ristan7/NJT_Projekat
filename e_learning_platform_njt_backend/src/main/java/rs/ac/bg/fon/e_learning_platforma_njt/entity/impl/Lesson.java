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
public class Lesson implements MyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lesson_id")
    private Long lessonId;

    @NotBlank
    @Size(max = 150)
    @Column(name = "lesson_title", nullable = false, length = 150)
    private String lessonTitle;

    @Size(max = 300)
    @Column(name = "lesson_summary", length = 300)
    private String lessonSummary;

    @Positive
    @Column(name = "lesson_order_index", nullable = false)
    private Integer lessonOrderIndex;

    @Column(name = "lesson_available", nullable = false)
    private boolean lessonAvailable = false;

    /**
     * Ako je true i lekcija je available, student sme bez enrolementa
     */
    @Column(name = "free_preview", nullable = false)
    private boolean freePreview = false;

    @PastOrPresent
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PastOrPresent
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

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

    public Lesson() {
    }

    public Lesson(Long lessonId) {
        this.lessonId = lessonId;
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
                + ", order=" + lessonOrderIndex
                + ", available=" + lessonAvailable
                + ", preview=" + freePreview
                + '}';
    }
}
