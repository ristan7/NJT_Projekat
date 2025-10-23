package rs.ac.bg.fon.e_learning_platforma_njt.entity.lookup;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.util.Objects;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.MyEntity;

@JsonIgnoreProperties({"courses"})
@Entity
@Table(name = "course_status")
public class CourseStatus implements MyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_status_id")
    private Long courseStatusId;

    @Column(name = "course_status_name", nullable = false, unique = true, length = 50)
    private String courseStatusName;

    public CourseStatus() {
    }

    public CourseStatus(Long courseStatusId) {
        this.courseStatusId = courseStatusId;
    }

    public CourseStatus(String courseStatusName) {
        this.courseStatusName = courseStatusName;
    }

    public Long getCourseStatusId() {
        return courseStatusId;
    }

    public void setCourseStatusId(Long courseStatusId) {
        this.courseStatusId = courseStatusId;
    }

    public String getCourseStatusName() {
        return courseStatusName;
    }

    public void setCourseStatusName(String courseStatusName) {
        this.courseStatusName = courseStatusName;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        CourseStatus other = (CourseStatus) obj;
        return Objects.equals(courseStatusId, other.courseStatusId)
                && Objects.equals(courseStatusName, other.courseStatusName);
    }

    @Override
    public String toString() {
        return "CourseStatus: " + courseStatusName;
    }
}
