package rs.ac.bg.fon.e_learning_platforma_njt.entity.lookup;

import jakarta.persistence.*;
import java.util.Objects;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.MyEntity;

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
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CourseStatus other = (CourseStatus) obj;
        if (!Objects.equals(this.courseStatusName, other.courseStatusName)) {
            return false;
        }
        return Objects.equals(this.courseStatusId, other.courseStatusId);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("CourseStatus: ").append(courseStatusName);
        return sb.toString();
    }

}
