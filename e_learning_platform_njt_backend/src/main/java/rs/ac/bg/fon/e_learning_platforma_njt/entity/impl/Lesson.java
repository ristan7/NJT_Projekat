/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rs.ac.bg.fon.e_learning_platforma_njt.entity.impl;

import java.io.Serializable;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.MyEntity;
import jakarta.persistence.*;
import java.util.List;
import java.util.Objects;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.lookup.LessonType;

/**
 *
 * @author mikir
 */
@Entity
@Table(name = "lesson")
public class Lesson implements Serializable, MyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lesson_id")
    private Long lessonId;

    @Column(name = "lesson_title", nullable = false, length = 100)
    private String lessonTitle;

    @Column(name = "lesson_order_index", nullable = false)
    private Integer lessonOrderIndex;

    @ManyToOne(optional = false)
    @JoinColumn(name = "lesson_type_id", nullable = false)
    private LessonType lessonType;

    @ManyToOne(optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Material> materials;

    public Lesson() {
    }

    public Lesson(Long lessonId) {
        this.lessonId = lessonId;
    }

    public Lesson(Long lessonId, String lessonTitle, Integer lessonOrderIndex, LessonType lessonType, Course course, List<Material> materials) {
        this.lessonId = lessonId;
        this.lessonTitle = lessonTitle;
        this.lessonOrderIndex = lessonOrderIndex;
        this.lessonType = lessonType;
        this.course = course;
        this.materials = materials;
    }

    public Lesson(String lessonTitle, Integer lessonOrderIndex, LessonType lessonType, Course course, List<Material> materials) {
        this.lessonTitle = lessonTitle;
        this.lessonOrderIndex = lessonOrderIndex;
        this.lessonType = lessonType;
        this.course = course;
        this.materials = materials;
    }

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

    public Integer getLessonOrderIndex() {
        return lessonOrderIndex;
    }

    public void setLessonOrderIndex(Integer lessonOrderIndex) {
        this.lessonOrderIndex = lessonOrderIndex;
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

    @Override
    public int hashCode() {
        int hash = 5;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Lesson other = (Lesson) obj;
        return Objects.equals(this.lessonId, other.lessonId);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Lesson: ");
        if (lessonOrderIndex != null) {
            sb.append("[").append(lessonOrderIndex).append("] ");
        }
        if (lessonTitle != null) {
            sb.append(lessonTitle);
        }
        if (lessonType != null) {
            sb.append(" (").append(lessonType.getLessonTypeName()).append(")");
        }
        return sb.toString();
    }
}
