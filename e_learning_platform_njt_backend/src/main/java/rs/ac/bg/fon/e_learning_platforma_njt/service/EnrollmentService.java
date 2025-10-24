package rs.ac.bg.fon.e_learning_platforma_njt.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import rs.ac.bg.fon.e_learning_platforma_njt.dto.impl.EnrollmentDto;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.impl.Course;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.impl.Enrollment;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.impl.User;
import rs.ac.bg.fon.e_learning_platforma_njt.mapper.impl.EnrollmentMapper;
import rs.ac.bg.fon.e_learning_platforma_njt.repository.impl.CourseRepository;
import rs.ac.bg.fon.e_learning_platforma_njt.repository.impl.EnrollmentRepository;
import rs.ac.bg.fon.e_learning_platforma_njt.repository.impl.UserRepository;

@Service
public class EnrollmentService {

    private final EnrollmentRepository enrollRepo;
    private final CourseRepository courseRepo;
    private final UserRepository userRepo;
    private final EnrollmentMapper mapper;

    private final RoleAccessService roleAccess;
    private final StatusIds statusIds;

    public EnrollmentService(EnrollmentRepository enrollRepo,
            CourseRepository courseRepo,
            UserRepository userRepo,
            EnrollmentMapper mapper,
            RoleAccessService roleAccess,
            StatusIds statusIds) {
        this.enrollRepo = enrollRepo;
        this.courseRepo = courseRepo;
        this.userRepo = userRepo;
        this.mapper = mapper;
        this.roleAccess = roleAccess;
        this.statusIds = statusIds;
    }

    /* =============================== READ =============================== */
    public EnrollmentDto findById(Long enrollmentId, Long requesterId, String roleName) throws Exception {
        Enrollment e = enrollRepo.findByIdWithCourseAndStudent(enrollmentId);
        ensureCanView(e, requesterId, roleName);
        return mapper.toDto(e);
    }

    public List<EnrollmentDto> listForStudent(Long studentId, Long statusId, int offset, int limit,
            Long requesterId, String roleName) throws Exception {
        ensureSelfOrAdmin(studentId, requesterId, roleName);
        return enrollRepo.findAllByStudentFiltered(studentId, statusId, offset, limit)
                .stream().map(mapper::toDto).collect(Collectors.toList());
    }

    public List<EnrollmentDto> listForTeacher(Long teacherId, Long statusId, int offset, int limit,
            Long requesterId, String roleName) throws Exception {
        ensureTeacherOrAdmin(roleName);
        ensureSameTeacher(teacherId, requesterId, roleName);
        return enrollRepo.findAllForTeacher(teacherId, statusId, offset, limit)
                .stream().map(mapper::toDto).collect(Collectors.toList());
    }

    public List<EnrollmentDto> listByCourse(Long courseId, Long requesterId, String roleName) throws Exception {
        Course course = courseRepo.findById(courseId);
        ensureTeacherOwnsCourse(requesterId, roleName, course);
        return enrollRepo.findAllByCourseId(courseId)
                .stream().map(mapper::toDto).collect(Collectors.toList());
    }

    /* =============================== WRITE ============================== */
    /**
     * Student podnosi zahtev za upis (REQUESTED).
     */
    @Transactional
    public EnrollmentDto requestEnrollment(Long courseId, Long studentId, String roleName) throws Exception {
        ensureStudent(roleName);

        Course course = courseRepo.findById(courseId);
        if (course == null) {
            throw new AccessDeniedException("Course not found.");
        }
        User student = userRepo.findById(studentId);
        if (student == null) {
            throw new AccessDeniedException("Student not found.");
        }

        // zabrani dupli ACTIVE
        if (enrollRepo.existsActive(studentId, courseId, statusIds.active())) {
            throw new IllegalStateException("Student already has an active enrollment for this course.");
        }

        // idempotentno za već REQUESTED
        Enrollment existing = enrollRepo.findByStudentAndCourse(studentId, courseId);
        if (existing != null && existing.getStatus() != null
                && Objects.equals(existing.getStatus().getEnrollmentStatusId(), statusIds.requested())) {
            return mapper.toDto(enrollRepo.findByIdWithCourseAndStudent(existing.getEnrollmentId()));
        }

        Enrollment e = new Enrollment();
        e.setStudent(new User(studentId));
        e.setCourse(new Course(courseId));
        e.setEnrolledAt(LocalDateTime.now());
        e.setStatus(new rs.ac.bg.fon.e_learning_platforma_njt.entity.lookup.EnrollmentStatus(statusIds.requested()));

        enrollRepo.save(e);
        return mapper.toDto(e);
    }

    /**
     * Aktivacija (ACTIVE) – teacher svog kursa ili admin.
     */
    @Transactional
    public EnrollmentDto activate(Long enrollmentId, Long actorId, String roleName) throws Exception {
        ensureTeacherOrAdmin(roleName);

        Enrollment e = enrollRepo.findByIdWithCourseAndStudent(enrollmentId);
        ensureTeacherOwnsCourse(actorId, roleName, e.getCourse());

        enrollRepo.updateStatus(enrollmentId, statusIds.active(), null, null);
        return mapper.toDto(enrollRepo.findByIdWithCourseAndStudent(enrollmentId));
    }

    /**
     * Otkazivanje (CANCELLED) – student sopstvenog ili teacher/admin.
     */
    @Transactional
    public EnrollmentDto cancel(Long enrollmentId, Long actorId, String roleName) throws Exception {
        Enrollment e = enrollRepo.findByIdWithCourseAndStudent(enrollmentId);

        boolean isOwnerStudent = e.getStudent() != null
                && Objects.equals(e.getStudent().getUserId(), actorId)
                && roleAccess.isStudent(roleName);

        if (!isOwnerStudent) {
            ensureTeacherOrAdmin(roleName);
            ensureTeacherOwnsCourse(actorId, roleName, e.getCourse());
        }

        enrollRepo.markCancelled(enrollmentId, statusIds.cancelled(), LocalDateTime.now());
        return mapper.toDto(enrollRepo.findByIdWithCourseAndStudent(enrollmentId));
    }

    /**
     * Završavanje (COMPLETED) – teacher svog kursa ili admin.
     */
    @Transactional
    public EnrollmentDto complete(Long enrollmentId, Long actorId, String roleName) throws Exception {
        ensureTeacherOrAdmin(roleName);

        Enrollment e = enrollRepo.findByIdWithCourseAndStudent(enrollmentId);
        ensureTeacherOwnsCourse(actorId, roleName, e.getCourse());

        enrollRepo.markCompleted(enrollmentId, statusIds.completed(), LocalDateTime.now());
        return mapper.toDto(enrollRepo.findByIdWithCourseAndStudent(enrollmentId));
    }

    /* ======================== Guards / Helpers ======================== */
    private void ensureStudent(String roleName) throws AccessDeniedException {
        if (!roleAccess.isStudent(roleName)) {
            throw new AccessDeniedException("Only STUDENT can request an enrollment.");
        }
    }

    private void ensureTeacherOrAdmin(String roleName) throws AccessDeniedException {
        if (!(roleAccess.isTeacher(roleName) || roleAccess.isAdmin(roleName))) {
            throw new AccessDeniedException("Only TEACHER or ADMIN can perform this action.");
        }
    }

    private void ensureTeacherOwnsCourse(Long teacherId, String roleName, Course course) throws AccessDeniedException {
        if (course == null) {
            throw new AccessDeniedException("Course not found.");
        }
        if (roleAccess.isAdmin(roleName)) {
            return;
        }

        if (roleAccess.isTeacher(roleName)) {
            if (course.getAuthor() == null || !Objects.equals(course.getAuthor().getUserId(), teacherId)) {
                throw new AccessDeniedException("You can modify only enrollments for your own courses.");
            }
        } else {
            throw new AccessDeniedException("Forbidden.");
        }
    }

    private void ensureSelfOrAdmin(Long targetUserId, Long requesterId, String roleName) throws AccessDeniedException {
        boolean self = Objects.equals(targetUserId, requesterId);
        if (!(self || roleAccess.isAdmin(roleName))) {
            throw new AccessDeniedException("Forbidden.");
        }
    }

    private void ensureSameTeacher(Long pathTeacherId, Long requesterId, String roleName) throws AccessDeniedException {
        if (roleAccess.isTeacher(roleName) && !Objects.equals(pathTeacherId, requesterId)) {
            throw new AccessDeniedException("Teacher can view only their own enrollments.");
        }
    }

    private void ensureCanView(Enrollment e, Long requesterId, String roleName) throws AccessDeniedException {
        if (e == null) {
            throw new AccessDeniedException("Enrollment not found.");
        }
        if (roleAccess.isAdmin(roleName)) {
            return;
        }

        if (roleAccess.isTeacher(roleName)) {
            ensureTeacherOwnsCourse(requesterId, roleName, e.getCourse());
            return;
        }

        if (roleAccess.isStudent(roleName)) {
            Long sid = (e.getStudent() != null) ? e.getStudent().getUserId() : null;
            if (!Objects.equals(sid, requesterId)) {
                throw new AccessDeniedException("Students can only view their own enrollments.");
            }
            return;
        }
        throw new AccessDeniedException("Forbidden.");
    }
}
