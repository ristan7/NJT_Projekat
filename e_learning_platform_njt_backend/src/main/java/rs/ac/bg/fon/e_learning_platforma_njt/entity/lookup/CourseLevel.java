package rs.ac.bg.fon.e_learning_platforma_njt.entity.lookup;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.util.Objects;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.MyEntity;

@JsonIgnoreProperties({"courses"}) // sprečava kružnu serializaciju sa Course
@Entity
@Table(name = "course_level")
public class CourseLevel implements MyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_level_id")
    private Long courseLevelId;

    @Column(name = "course_level_name", nullable = false, unique = true, length = 50)
    private String courseLevelName;

    public CourseLevel() {
    }

    public CourseLevel(Long courseLevelId) {
        this.courseLevelId = courseLevelId;
    }

    public CourseLevel(String courseLevelName) {
        this.courseLevelName = courseLevelName;
    }

    public Long getCourseLevelId() {
        return courseLevelId;
    }

    public void setCourseLevelId(Long courseLevelId) {
        this.courseLevelId = courseLevelId;
    }

    public String getCourseLevelName() {
        return courseLevelName;
    }

    public void setCourseLevelName(String courseLevelName) {
        this.courseLevelName = courseLevelName;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        CourseLevel other = (CourseLevel) obj;
        return Objects.equals(courseLevelId, other.courseLevelId)
                && Objects.equals(courseLevelName, other.courseLevelName);
    }

    @Override
    public String toString() {
        return "CourseLevel: " + courseLevelName;
    }
}
