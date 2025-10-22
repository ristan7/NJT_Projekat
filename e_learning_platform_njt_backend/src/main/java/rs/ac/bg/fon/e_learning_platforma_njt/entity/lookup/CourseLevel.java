package rs.ac.bg.fon.e_learning_platforma_njt.entity.lookup;

import jakarta.persistence.*;
import java.util.Objects;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.MyEntity;

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
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CourseLevel other = (CourseLevel) obj;
        if (!Objects.equals(this.courseLevelName, other.courseLevelName)) {
            return false;
        }
        return Objects.equals(this.courseLevelId, other.courseLevelId);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("CourseLevel: ").append(courseLevelName);
        return sb.toString();
    }

}
