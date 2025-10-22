package rs.ac.bg.fon.e_learning_platforma_njt.entity.lookup;

import jakarta.persistence.*;
import java.util.Objects;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.MyEntity;

@Entity
@Table(name = "enrollment_status")
public class EnrollmentStatus implements MyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "enrollment_status_id")
    private Long enrollmentStatusId;

    @Column(name = "enrollment_status_name", nullable = false, unique = true, length = 50)
    private String enrollmentStatusName;

    public EnrollmentStatus() {
    }

    public EnrollmentStatus(String enrollmentStatusName) {
        this.enrollmentStatusName = enrollmentStatusName;
    }

    public Long getEnrollmentStatusId() {
        return enrollmentStatusId;
    }

    public void setEnrollmentStatusId(Long enrollmentStatusId) {
        this.enrollmentStatusId = enrollmentStatusId;
    }

    public String getEnrollmentStatusName() {
        return enrollmentStatusName;
    }

    public void setEnrollmentStatusName(String enrollmentStatusName) {
        this.enrollmentStatusName = enrollmentStatusName;
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
        final EnrollmentStatus other = (EnrollmentStatus) obj;
        if (!Objects.equals(this.enrollmentStatusName, other.enrollmentStatusName)) {
            return false;
        }
        return Objects.equals(this.enrollmentStatusId, other.enrollmentStatusId);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("EnrollmentStatus: ").append(enrollmentStatusName);
        return sb.toString();
    }
}
