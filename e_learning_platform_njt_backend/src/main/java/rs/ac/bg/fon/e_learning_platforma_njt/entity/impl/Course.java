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
import rs.ac.bg.fon.e_learning_platforma_njt.entity.lookup.CourseLevel;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.lookup.CourseStatus;

/**
 *
 * @author mikir
 */
@Entity
@Table(name = "course")
public class Course implements Serializable, MyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Long courseId;

    @Column(name = "course_title", nullable = false, length = 100)
    private String courseTitle;

    @Column(name = "course_description", columnDefinition = "TEXT")
    private String courseDescription;

    @ManyToOne(optional = false)
    @JoinColumn(name = "course_status_id", nullable = false)
    private CourseStatus status;

    @ManyToOne(optional = false)
    @JoinColumn(name = "course_level_id", nullable = false)
    private CourseLevel level;

    @ManyToOne(optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Lesson> lessons;

    public Course() {
    }

    public Course(Long courseId) {
        this.courseId = courseId;
    }

    public Course(Long courseId, String courseTitle, String courseDescription, CourseStatus status, CourseLevel level, User author, List<Lesson> lessons) {
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        this.courseDescription = courseDescription;
        this.status = status;
        this.level = level;
        this.author = author;
        this.lessons = lessons;
    }

    public Course(String courseTitle, String courseDescription, CourseStatus status, CourseLevel level, User author, List<Lesson> lessons) {
        this.courseTitle = courseTitle;
        this.courseDescription = courseDescription;
        this.status = status;
        this.level = level;
        this.author = author;
        this.lessons = lessons;
    }

    // getters/setters
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

    public CourseStatus getStatus() {
        return status;
    }

    public void setStatus(CourseStatus status) {
        this.status = status;
    }

    public CourseLevel getLevel() {
        return level;
    }

    public void setLevel(CourseLevel level) {
        this.level = level;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public List<Lesson> getLessons() {
        return lessons;
    }

    public void setLessons(List<Lesson> lessons) {
        this.lessons = lessons;
    }

    @Override
    public int hashCode() {
        int hash = 3;
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
        final Course other = (Course) obj;
        return Objects.equals(this.courseId, other.courseId);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Course: ")
                .append(courseTitle != null ? courseTitle : "")
                .append(" (ID: ").append(courseId).append(")");
        return sb.toString();
    }

}
