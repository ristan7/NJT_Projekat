package rs.ac.bg.fon.e_learning_platforma_njt.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import rs.ac.bg.fon.e_learning_platforma_njt.dto.impl.CourseDto;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.impl.Course;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.impl.User;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.lookup.CourseLevel;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.lookup.CourseStatus;
import rs.ac.bg.fon.e_learning_platforma_njt.mapper.impl.CourseMapper;
import rs.ac.bg.fon.e_learning_platforma_njt.repository.impl.CourseRepository;

/**
 * CourseService – CRUD i PATCH nad kursevima uz pravila uloga: - Student: read-only - Admin: read-only (može gledati sve bez ograničenja) - Teacher: full CRUD + PATCH nad sopstvenim kursevima
 */
@Service
public class CourseService {

    private final CourseRepository courseRepo;
    private final CourseMapper courseMapper;
    private final RoleAccessService roleAccess; // top-level bean

    public CourseService(CourseRepository courseRepo,
            CourseMapper courseMapper,
            RoleAccessService roleAccess) {
        this.courseRepo = courseRepo;
        this.courseMapper = courseMapper;
        this.roleAccess = roleAccess;
    }

    /* ===========================================================
       =========================== READ ===========================
       =========================================================== */
    /**
     * Lista svih kurseva (public).
     */
    public List<CourseDto> findAll() {
        return courseRepo.findAll().stream()
                .map(courseMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Detalj kursa (samo kurs).
     */
    public CourseDto findById(Long courseId) throws Exception {
        Course c = courseRepo.findById(courseId);
        return courseMapper.toDto(c);
    }

    /**
     * Kursevi jednog autora (Teacher dashboard).
     */
    public List<CourseDto> findAllByAuthor(Long authorId) {
        return courseRepo.findAllByAuthorId(authorId).stream()
                .map(courseMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Jednostavna pretraga po naslovu.
     */
    public List<CourseDto> searchByTitle(String q) {
        return courseRepo.searchByTitle(q).stream()
                .map(courseMapper::toDto)
                .collect(Collectors.toList());
    }

    /* ===========================================================
       =========================== WRITE ==========================
       =========================================================== */
    /**
     * Kreiranje kursa – TEACHER only. Kurs može biti prazan.
     */
    @Transactional
    public CourseDto create(CourseDto dto, Long requesterId, String roleName) throws IllegalAccessException {
        ensureTeacher(roleName);

        Course e = courseMapper.toEntity(dto);
        // autor je ulogovani teacher
        e.setAuthor(new User(requesterId));

        courseRepo.save(e); // persist
        return courseMapper.toDto(e);
    }

    /**
     * Izmena celog kursa – TEACHER only, i to samo svog kursa.
     */
    @Transactional
    public CourseDto update(Long courseId, CourseDto dto, Long requesterId, String roleName) throws Exception {
        ensureTeacher(roleName);

        Course existing = courseRepo.findById(courseId);
        ensureTeacherOwnsCourse(requesterId, existing);

        // Primeni izmene (bez datuma; publishedAt rešava entitet/status)
        existing.setCourseTitle(dto.getCourseTitle());
        existing.setCourseDescription(dto.getCourseDescription());

        if (dto.getAuthorId() != null && !Objects.equals(dto.getAuthorId(), requesterId)) {
            // zabrani promenu autora (teacher ne može da „premesti“ kurs na drugog autora)
            throw new IllegalAccessException("You cannot change course author.");
        }
        if (dto.getCourseLevelId() != null) {
            existing.setCourseLevel(new CourseLevel(dto.getCourseLevelId()));
        }
        if (dto.getCourseStatusId() != null) {
            existing.setCourseStatus(new CourseStatus(dto.getCourseStatusId()));
        }

        courseRepo.save(existing); // merge
        return courseMapper.toDto(existing);
    }

    /**
     * Brisanje kursa – TEACHER only, i to samo svog kursa.
     */
    @Transactional
    public void delete(Long courseId, Long requesterId, String roleName) throws Exception {
        ensureTeacher(roleName);

        Course existing = courseRepo.findById(courseId);
        ensureTeacherOwnsCourse(requesterId, existing);

        courseRepo.deleteById(courseId); // orphanRemoval nizvodno briše lekcije/materijale
    }

    /* ===========================================================
       =========================== PATCH ==========================
       =========================================================== */
    // PATCH radi TEACHER (samo nad sopstvenim kursem)
    @Transactional
    public CourseDto patchTitle(Long courseId, String newTitle, Long requesterId, String roleName) throws Exception {
        ensureTeacher(roleName);

        Course c = courseRepo.findById(courseId);
        ensureTeacherOwnsCourse(requesterId, c);

        c.setCourseTitle(newTitle);
        courseRepo.save(c);
        return courseMapper.toDto(c);
    }

    @Transactional
    public CourseDto patchDescription(Long courseId, String newDescription, Long requesterId, String roleName) throws Exception {
        ensureTeacher(roleName);

        Course c = courseRepo.findById(courseId);
        ensureTeacherOwnsCourse(requesterId, c);

        c.setCourseDescription(newDescription);
        courseRepo.save(c);
        return courseMapper.toDto(c);
    }

    @Transactional
    public CourseDto patchLevel(Long courseId, Long levelId, Long requesterId, String roleName) throws Exception {
        ensureTeacher(roleName);

        Course c = courseRepo.findById(courseId);
        ensureTeacherOwnsCourse(requesterId, c);

        c.setCourseLevel(levelId != null ? new CourseLevel(levelId) : null);
        courseRepo.save(c);
        return courseMapper.toDto(c);
    }

    @Transactional
    public CourseDto patchStatus(Long courseId, Long statusId, Long requesterId, String roleName) throws Exception {
        ensureTeacher(roleName);

        Course c = courseRepo.findById(courseId);
        ensureTeacherOwnsCourse(requesterId, c);

        c.setCourseStatus(statusId != null ? new CourseStatus(statusId) : null);
        // Pretpostavka: entitet Course u setteru/PreUpdate brine o publishedAt ako status → PUBLISHED
        courseRepo.save(c);
        return courseMapper.toDto(c);
    }

    /* ===========================================================
       ====================== Helper / Guard ======================
       =========================================================== */
    private void ensureTeacher(String roleName) throws IllegalAccessException {
        if (!roleAccess.isTeacher(roleName)) {
            throw new IllegalAccessException("Only TEACHER can modify courses.");
        }
    }

    private void ensureTeacherOwnsCourse(Long teacherId, Course course) throws IllegalAccessException {
        if (course == null || course.getAuthor() == null
                || !Objects.equals(course.getAuthor().getUserId(), teacherId)) {
            throw new IllegalAccessException("You can modify only your own courses.");
        }
    }
}
