package rs.ac.bg.fon.e_learning_platforma_njt.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import rs.ac.bg.fon.e_learning_platforma_njt.dto.impl.LessonDto;
import rs.ac.bg.fon.e_learning_platforma_njt.dto.impl.MaterialDto;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.impl.Course;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.impl.Lesson;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.impl.Material;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.lookup.LessonType;
import rs.ac.bg.fon.e_learning_platforma_njt.mapper.impl.LessonMapper;
import rs.ac.bg.fon.e_learning_platforma_njt.mapper.impl.MaterialMapper;
import rs.ac.bg.fon.e_learning_platforma_njt.repository.impl.LessonRepository;
import rs.ac.bg.fon.e_learning_platforma_njt.repository.impl.CourseRepository;

/**
 * LessonService – CRUD nad lekcijama i sve operacije nad materijalima (write ide kroz lekciju). Pristup: - Student: read samo ako je kurs plaćen (inače 403). - Teacher: full CRUD/patch nad lekcijama i materijalima svog kursa. - Admin: read only, bez ograničenja na plaćanje.
 */
@Service
public class LessonService {

    private final LessonRepository lessonRepo;
    private final CourseRepository courseRepo;
    private final LessonMapper lessonMapper;
    private final MaterialMapper materialMapper;

    // Hook-ovi ka auth/payment sloju
    private final PaymentAccessService paymentAccessService; // hasPaid(userId, courseId)
    private final RoleAccessService roleAccessService;       // helper za role

    public LessonService(LessonRepository lessonRepo,
            CourseRepository courseRepo,
            LessonMapper lessonMapper,
            MaterialMapper materialMapper,
            PaymentAccessService paymentAccessService,
            RoleAccessService roleAccessService) {
        this.lessonRepo = lessonRepo;
        this.courseRepo = courseRepo;
        this.lessonMapper = lessonMapper;
        this.materialMapper = materialMapper;
        this.paymentAccessService = paymentAccessService;
        this.roleAccessService = roleAccessService;
    }

    /* ===========================================================
       ======================== READ ==============================
       =========================================================== */
    /**
     * Sve lekcije za kurs – student samo ako je platio; admin/teacher uvek.
     */
    public List<LessonDto> findAllByCourse(Long courseId, Long requesterId, String roleName) throws Exception {
        Course course = courseRepo.findById(courseId);

        if (roleAccessService.isStudent(roleName)) {
            if (!paymentAccessService.hasPaid(requesterId, courseId)) {
                throw new AccessDeniedException("Course not purchased.");
            }
        }
        return lessonRepo.findAllByCourseId(courseId)
                .stream()
                .map(lessonMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Detalj lekcije – student samo ako je platio; admin/teacher uvek.
     */
    public LessonDto findById(Long lessonId, Long requesterId, String roleName) throws Exception {
        Lesson lesson = lessonRepo.findByIdWithMaterials(lessonId);
        Long courseId = lesson.getCourse().getCourseId();

        if (roleAccessService.isStudent(roleName)) {
            if (!paymentAccessService.hasPaid(requesterId, courseId)) {
                throw new AccessDeniedException("Course not purchased.");
            }
        }
        return lessonMapper.toDto(lesson);
    }

    /**
     * Lista materijala za lekciju – isti uslovi pristupa kao i za findById.
     */
    public List<MaterialDto> findMaterialsByLesson(Long lessonId, Long requesterId, String roleName) throws Exception {
        Lesson lesson = lessonRepo.findByIdWithMaterials(lessonId);
        Long courseId = lesson.getCourse().getCourseId();

        if (roleAccessService.isStudent(roleName)) {
            if (!paymentAccessService.hasPaid(requesterId, courseId)) {
                throw new AccessDeniedException("Course not purchased.");
            }
        }
        return lesson.getMaterials()
                .stream()
                .map(materialMapper::toDto)
                .collect(Collectors.toList());
    }

    /* ===========================================================
       ======================== WRITE =============================
       =========================================================== */
    /**
     * Kreiraj novu lekciju u kursu (na kraj liste ako order nije zadan).
     */
    @Transactional
    public LessonDto create(Long courseId, LessonDto dto, Long teacherId) throws Exception {
        Course course = courseRepo.findById(courseId);
        ensureTeacherOwnsCourse(teacherId, course);

        Lesson entity = lessonMapper.toEntity(dto);
        entity.setCourse(course);

        if (entity.getLessonOrderIndex() == null) {
            int next = lessonRepo.getNextOrderIndexForCourse(courseId);
            entity.setLessonOrderIndex(next);
        }
        lessonRepo.save(entity);
        return lessonMapper.toDto(entity);
    }

    /**
     * Izmeni postojeću lekciju (naslov, summary, order, tip...).
     */
    @Transactional
    public LessonDto update(Long lessonId, LessonDto dto, Long teacherId) throws Exception {
        Lesson existing = lessonRepo.findById(lessonId);
        ensureTeacherOwnsCourse(teacherId, existing.getCourse());

        lessonMapper.apply(dto, existing);
        lessonRepo.save(existing);
        return lessonMapper.toDto(existing);
    }

    /**
     * PATCH – dostupnost lekcije.
     */
    @Transactional
    public LessonDto patchAvailability(Long lessonId, boolean available, Long teacherId) throws Exception {
        Lesson existing = lessonRepo.findById(lessonId);
        ensureTeacherOwnsCourse(teacherId, existing.getCourse());

        existing.setLessonAvailable(available);
        lessonRepo.save(existing);
        return lessonMapper.toDto(existing);
    }

    /**
     * PATCH – promena tipa lekcije (lookup id).
     */
    @Transactional
    public LessonDto patchType(Long lessonId, Long lessonTypeId, Long teacherId) throws Exception {
        Lesson existing = lessonRepo.findById(lessonId);
        ensureTeacherOwnsCourse(teacherId, existing.getCourse());

        existing.setLessonType(lessonTypeId != null ? new LessonType(lessonTypeId) : null);
        lessonRepo.save(existing);
        return lessonMapper.toDto(existing);
    }

    /**
     * Brisanje lekcije.
     */
    @Transactional
    public void delete(Long lessonId, Long teacherId) throws Exception {
        Lesson existing = lessonRepo.findById(lessonId);
        ensureTeacherOwnsCourse(teacherId, existing.getCourse());
        lessonRepo.deleteById(lessonId);
    }

    /* ===========================================================
       ============== MATERIAL OPERACIJE (preko Lesson) ==========
       =========================================================== */
    /**
     * Dodaj materijal u lekciju (na kraj ako index nije dat).
     */
    @Transactional
    public MaterialDto addMaterial(Long lessonId, MaterialDto dto, Long teacherId) throws Exception {
        Lesson lesson = lessonRepo.findByIdWithMaterials(lessonId);
        ensureTeacherOwnsCourse(teacherId, lesson.getCourse());

        Material m = materialMapper.toEntity(dto);
        m.setLesson(lesson);

        if (m.getMaterialOrderIndex() == null) {
            int next = (lesson.getMaterials() != null ? lesson.getMaterials().size() : 0) + 1;
            m.setMaterialOrderIndex(next);
        }
        lesson.getMaterials().add(m);
        lessonRepo.save(lesson);
        return materialMapper.toDto(m);
    }

    /**
     * Izmeni postojeći materijal (naslov, sadržaj, url, tip, order...).
     */
    @Transactional
    public MaterialDto updateMaterial(Long lessonId, MaterialDto dto, Long teacherId) throws Exception {
        Lesson lesson = lessonRepo.findByIdWithMaterials(lessonId);
        ensureTeacherOwnsCourse(teacherId, lesson.getCourse());

        Material m = lesson.getMaterials().stream()
                .filter(x -> Objects.equals(x.getMaterialId(), dto.getMaterialId()))
                .findFirst()
                .orElseThrow(() -> new Exception("Material not found: " + dto.getMaterialId()));

        materialMapper.apply(dto, m);
        lessonRepo.save(lesson);
        return materialMapper.toDto(m);
    }

    /**
     * Ukloni materijal iz lekcije.
     */
    @Transactional
    public void removeMaterial(Long lessonId, Long materialId, Long teacherId) throws Exception {
        Lesson lesson = lessonRepo.findByIdWithMaterials(lessonId);
        ensureTeacherOwnsCourse(teacherId, lesson.getCourse());

        boolean removed = lesson.getMaterials().removeIf(m -> Objects.equals(m.getMaterialId(), materialId));
        if (!removed) {
            throw new Exception("Material not found: " + materialId);
        }

        lessonRepo.save(lesson);
    }

    /* ===========================================================
       ===================== Helperi / Guard ======================
       =========================================================== */
    private void ensureTeacherOwnsCourse(Long teacherId, Course course) throws IllegalAccessException {
        if (course == null || course.getAuthor() == null
                || !Objects.equals(course.getAuthor().getUserId(), teacherId)) {
            throw new IllegalAccessException("Only the course author (teacher) can modify this resource.");
        }
        // dodatno: mogao bi se zabraniti write ako je course ARCHIVED (po želji)
    }

    /* ==================== Hook interfejsi ===================== */
    /**
     * Provera da li student ima pravo pristupa kursu (platio/upisan).
     */
    public interface PaymentAccessService {

        boolean hasPaid(Long userId, Long courseId);
    }

    /**
     * Helper za uloge (ADMIN/TEACHER/STUDENT).
     */
    public interface RoleAccessService {

        boolean isAdmin(String roleName);

        boolean isTeacher(String roleName);

        boolean isStudent(String roleName);
    }
}
