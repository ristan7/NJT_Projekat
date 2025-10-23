package rs.ac.bg.fon.e_learning_platforma_njt.entity.impl;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.MyEntity;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.lookup.CourseLevel;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.lookup.CourseStatus;

@Entity
@Table(name = "course",
        indexes = {
            @Index(name = "ix_course_author", columnList = "author_id"),
            @Index(name = "ix_course_status", columnList = "course_status_id"),
            @Index(name = "ix_course_level", columnList = "course_level_id")
        })
public class Course implements MyEntity {

    /* ===================== Polja ===================== */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Long courseId;

    @NotBlank(message = "Course title is required.")
    @Size(max = 150, message = "Course title can be at most 150 characters.")
    @Column(name = "course_title", nullable = false, length = 150)
    private String courseTitle;

    @NotBlank(message = "Course description is required.")
    @Column(name = "course_description", columnDefinition = "TEXT", nullable = false)
    private String courseDescription;

    @Digits(integer = 8, fraction = 2)
    @PositiveOrZero(message = "Course price must be non-negative.")
    @Column(name = "course_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal coursePrice = BigDecimal.ZERO;

    /* ====== DATUMI ====== */
    @PastOrPresent
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PastOrPresent
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    /* ====== RELACIJE ====== */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_course_author"))
    private User author;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_level_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_course_level"))
    private CourseLevel courseLevel;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_status_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_course_status"))
    private CourseStatus courseStatus;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("lessonOrderIndex ASC")
    private List<Lesson> lessons = new ArrayList<>();

    /* ====== Lifecycle hook-ovi za datume ====== */
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

    public void setCourseStatus(CourseStatus courseStatus) {
        boolean publishing = courseStatus != null
                && "PUBLISHED".equalsIgnoreCase(courseStatus.getCourseStatusName());
        if (publishing && this.publishedAt == null) {
            this.publishedAt = LocalDateTime.now();
        }
        this.courseStatus = courseStatus;
    }

    /* ====== Konstruktori ====== */
    public Course() {
    }

    public Course(Long courseId) {
        this.courseId = courseId;
    }

    /* ====== Getteri i Setteri ====== */
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

    public BigDecimal getCoursePrice() {
        return coursePrice;
    }

    public void setCoursePrice(BigDecimal coursePrice) {
        this.coursePrice = coursePrice;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public CourseLevel getCourseLevel() {
        return courseLevel;
    }

    public void setCourseLevel(CourseLevel courseLevel) {
        this.courseLevel = courseLevel;
    }

    public CourseStatus getCourseStatus() {
        return courseStatus;
    }

    public List<Lesson> getLessons() {
        return lessons;
    }

    public void setLessons(List<Lesson> lessons) {
        this.lessons = lessons;
    }

    public void addLesson(Lesson l) {
        if (l == null) {
            return;
        }
        lessons.add(l);
        l.setCourse(this);
    }

    public void removeLesson(Lesson l) {
        if (l == null) {
            return;
        }
        lessons.remove(l);
        l.setCourse(null);
    }

    /* ====== equals, hashCode, toString ====== */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Course)) {
            return false;
        }
        Course other = (Course) o;
        return Objects.equals(courseId, other.courseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseId);
    }

    @Override
    public String toString() {
        return "Course{"
                + "courseId=" + courseId
                + ", courseTitle='" + courseTitle + '\''
                + ", coursePrice=" + coursePrice
                + ", courseStatus=" + (courseStatus != null ? courseStatus.getCourseStatusName() : "null")
                + ", courseLevel=" + (courseLevel != null ? courseLevel.getCourseLevelName() : "null")
                + '}';
    }
}
