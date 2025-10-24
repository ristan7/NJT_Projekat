package rs.ac.bg.fon.e_learning_platforma_njt.mapper.impl;

import org.springframework.stereotype.Component;
import rs.ac.bg.fon.e_learning_platforma_njt.dto.impl.EnrollmentDto;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.impl.Course;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.impl.Enrollment;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.impl.User;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.lookup.EnrollmentStatus;
import rs.ac.bg.fon.e_learning_platforma_njt.mapper.DtoEntityMapper;

@Component
public class EnrollmentMapper implements DtoEntityMapper<EnrollmentDto, Enrollment> {

    @Override
    public EnrollmentDto toDto(Enrollment e) {
        if (e == null) {
            return null;
        }

        EnrollmentDto dto = new EnrollmentDto();

        // Osnovni ID-jevi
        dto.setEnrollmentId(e.getEnrollmentId());
        dto.setStudentId(e.getStudent() != null ? e.getStudent().getUserId() : null);
        dto.setCourseId(e.getCourse() != null ? e.getCourse().getCourseId() : null);
        dto.setStatusId(e.getStatus() != null ? e.getStatus().getEnrollmentStatusId() : null);

        // READ-ONLY podaci
        dto.setStudentUsername(e.getStudent() != null ? e.getStudent().getUsername() : null);
        dto.setStudentEmail(e.getStudent() != null ? e.getStudent().getEmail() : null);
        dto.setCourseTitle(e.getCourse() != null ? e.getCourse().getCourseTitle() : null);
        dto.setStatusName(e.getStatus() != null ? e.getStatus().getEnrollmentStatusName() : null);

        dto.setEnrolledAt(e.getEnrolledAt());
        dto.setLastAccessedAt(e.getLastAccessedAt());

        return dto;
    }

    @Override
    public Enrollment toEntity(EnrollmentDto t) {
        if (t == null) {
            return null;
        }

        Enrollment e = new Enrollment();
        e.setEnrollmentId(t.getEnrollmentId()); // null kod create, setovano kod update

        if (t.getStudentId() != null) {
            e.setStudent(new User(t.getStudentId()));
        }
        if (t.getCourseId() != null) {
            e.setCourse(new Course(t.getCourseId()));
        }
        if (t.getStatusId() != null) {
            e.setStatus(new EnrollmentStatus(t.getStatusId()));
        }

        // Datumi (enrolledAt, lastAccessedAt) ostaju pod kontrolom entiteta
        return e;
    }

    /**
     * Partial UPDATE: - Ne menjamo studenta ni kurs (sprečava premeštanje upisa). - Status menjamo samo ako je prosleđen ID. - Datume ne diramo (enrolledAt/lastAccessedAt).
     */
    @Override
    public void apply(EnrollmentDto t, Enrollment e) {
        if (t == null || e == null) {
            return;
        }

        if (t.getStatusId() != null) {
            e.setStatus(new EnrollmentStatus(t.getStatusId()));
        }

        // Student/kurs se ne menjaju
        // e.setStudent(...) / e.setCourse(...) — nikad ne pozivamo ovde
        // Datumi ostaju netaknuti; updatedAt osvežava @PreUpdate u entitetu ako postoji
    }
}
