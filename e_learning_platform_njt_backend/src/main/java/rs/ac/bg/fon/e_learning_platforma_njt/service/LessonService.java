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
import rs.ac.bg.fon.e_learning_platforma_njt.entity.lookup.LessonType;
import rs.ac.bg.fon.e_learning_platforma_njt.mapper.impl.LessonMapper;
import rs.ac.bg.fon.e_learning_platforma_njt.mapper.impl.MaterialMapper;
import rs.ac.bg.fon.e_learning_platforma_njt.repository.impl.LessonRepository;
import rs.ac.bg.fon.e_learning_platforma_njt.repository.impl.CourseRepository;
import rs.ac.bg.fon.e_learning_platforma_njt.repository.impl.EnrollmentRepository;

@Service
public class LessonService {

    private final LessonRepository lessonRepo;
    private final CourseRepository courseRepo;
    private final EnrollmentRepository enrollRepo;
    private final LessonMapper lessonMapper;
    private final MaterialMapper materialMapper;
    private final RoleAccessService roleAccess;
    private final StatusIds statusIds;

    public LessonService(LessonRepository lessonRepo,
            CourseRepository courseRepo,
            EnrollmentRepository enrollRepo,
            LessonMapper lessonMapper,
            MaterialMapper materialMapper,
            RoleAccessService roleAccess,
            StatusIds statusIds) {
        this.lessonRepo = lessonRepo;
        this.courseRepo = courseRepo;
        this.enrollRepo = enrollRepo;
        this.lessonMapper = lessonMapper;
        this.materialMapper = materialMapper;
        this.roleAccess = roleAccess;
        this.statusIds = statusIds;
    }

    /* =============================== READ =============================== */
    /**
     * Listing lekcija u kursu: - ADMIN/TEACHER (autor) → sve - STUDENT sa aktivnim enrollmentom → samo lessonAvailable=true - STUDENT bez enrolementa → samo lessonAvailable=true AND freePreview=true
     */
    public List<LessonDto> findAllByCourse(Long courseId, Long requesterId, String roleName) throws Exception {
        Course course = courseRepo.findById(courseId);
        if (course == null) {
            throw new AccessDeniedException("Course not found.");
        }

        if (roleAccess.isAdmin(roleName) || roleAccess.isTeacher(roleName)) {
            // Ako teacher, ali nije autor, i dalje može (po tvom modelu admin/teacher vide sve);
            // ako želiš da ograničiš teachera samo na svoje kurseve – dodaj check.
            return lessonRepo.findAllByCourseId(courseId).stream().map(lessonMapper::toDto).collect(Collectors.toList());
        }

        // STUDENT
        boolean hasEnroll = enrollRepo.existsActive(requesterId, courseId, statusIds.active());
        var list = hasEnroll
                ? lessonRepo.findVisibleForEnrolledStudent(courseId)
                : lessonRepo.findVisibleForGuestStudent(courseId);

        return list.stream().map(lessonMapper::toDto).collect(Collectors.toList());
    }

    /**
     * Detalj lekcije: - ADMIN/TEACHER → uvek - STUDENT: * ako lessonAvailable=false → 403 * ako freePreview=true → dozvoli * inače treba ACTIVE enrollment
     */
    public LessonDto findById(Long lessonId, Long requesterId, String roleName) throws Exception {
        Lesson lesson = lessonRepo.findByIdWithMaterials(lessonId);
        if (lesson == null) {
            throw new AccessDeniedException("Lesson not found.");
        }
        Long courseId = lesson.getCourse().getCourseId();

        if (roleAccess.isAdmin(roleName) || roleAccess.isTeacher(roleName)) {
            return lessonMapper.toDto(lesson);
        }

        // STUDENT
        if (!lesson.isLessonAvailable()) {
            throw new AccessDeniedException("Lesson is not published.");
        }
        if (lesson.isFreePreview()) {
            return lessonMapper.toDto(lesson);
        }
        boolean allowed = enrollRepo.existsActive(requesterId, courseId, statusIds.active());
        if (!allowed) {
            throw new AccessDeniedException("Course not accessible (no active enrollment).");
        }
        return lessonMapper.toDto(lesson);
    }

    /**
     * Materijali – isti uslovi kao za detalj.
     */
    public List<MaterialDto> findMaterialsByLesson(Long lessonId, Long requesterId, String roleName) throws Exception {
        Lesson lesson = lessonRepo.findByIdWithMaterials(lessonId);
        if (lesson == null) {
            throw new AccessDeniedException("Lesson not found.");
        }
        Long courseId = lesson.getCourse().getCourseId();

        if (roleAccess.isAdmin(roleName) || roleAccess.isTeacher(roleName)) {
            return lesson.getMaterials().stream().map(materialMapper::toDto).collect(Collectors.toList());
        }

        if (!lesson.isLessonAvailable()) {
            throw new AccessDeniedException("Lesson is not published.");
        }
        if (lesson.isFreePreview()) {
            return lesson.getMaterials().stream().map(materialMapper::toDto).collect(Collectors.toList());
        }
        boolean allowed = enrollRepo.existsActive(requesterId, courseId, statusIds.active());
        if (!allowed) {
            throw new AccessDeniedException("Course not accessible (no active enrollment).");
        }
        return lesson.getMaterials().stream().map(materialMapper::toDto).collect(Collectors.toList());
    }

    /* =============================== WRITE ============================== */
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

    @Transactional
    public LessonDto update(Long lessonId, LessonDto dto, Long teacherId) throws Exception {
        Lesson existing = lessonRepo.findById(lessonId);
        if (existing == null) {
            throw new AccessDeniedException("Lesson not found.");
        }

        ensureTeacherOwnsCourse(teacherId, existing.getCourse());
        lessonMapper.apply(dto, existing);
        lessonRepo.save(existing);
        return lessonMapper.toDto(existing);
    }

    @Transactional
    public LessonDto patchAvailability(Long lessonId, boolean available, Long teacherId) throws Exception {
        Lesson existing = lessonRepo.findById(lessonId);
        if (existing == null) {
            throw new AccessDeniedException("Lesson not found.");
        }

        ensureTeacherOwnsCourse(teacherId, existing.getCourse());
        existing.setLessonAvailable(available);
        lessonRepo.save(existing);
        return lessonMapper.toDto(existing);
    }

    @Transactional
    public LessonDto patchType(Long lessonId, Long lessonTypeId, Long teacherId) throws Exception {
        Lesson existing = lessonRepo.findById(lessonId);
        if (existing == null) {
            throw new AccessDeniedException("Lesson not found.");
        }

        ensureTeacherOwnsCourse(teacherId, existing.getCourse());
        existing.setLessonType(lessonTypeId != null ? new LessonType(lessonTypeId) : null);
        lessonRepo.save(existing);
        return lessonMapper.toDto(existing);
    }

    @Transactional
    public void delete(Long lessonId, Long teacherId) throws Exception {
        Lesson existing = lessonRepo.findById(lessonId);
        if (existing == null) {
            throw new AccessDeniedException("Lesson not found.");
        }

        ensureTeacherOwnsCourse(teacherId, existing.getCourse());
        lessonRepo.deleteById(lessonId);
    }

    /* =========================== Helpers =========================== */
    private void ensureTeacherOwnsCourse(Long teacherId, Course course) throws AccessDeniedException {
        if (course == null || course.getAuthor() == null
                || !Objects.equals(course.getAuthor().getUserId(), teacherId)) {
            throw new AccessDeniedException("Only the course author (teacher) can modify this resource.");
        }
    }

    @Transactional
    public List<MaterialDto> replaceMaterials(Long lessonId, List<MaterialDto> incoming, Long teacherId) throws Exception {
        // Uvek čitaj sa materijalima
        Lesson lesson = lessonRepo.findByIdWithMaterials(lessonId);
        if (lesson == null) {
            throw new AccessDeniedException("Lesson not found.");
        }

        // Dozvola: samo autor kursa
        ensureTeacherOwnsCourse(teacherId, lesson.getCourse());

        // Index postojećih po ID-u radi bržeg upoređivanja
        var existing = lesson.getMaterials();
        var byId = existing.stream()
                .filter(m -> m.getMaterialId() != null)
                .collect(java.util.stream.Collectors.toMap(m -> m.getMaterialId(), m -> m));

        // ID-evi koji ostaju posle obrade (sve što nije u setu biće uklonjeno)
        java.util.Set<Long> keepIds = new java.util.HashSet<>();

        // 1) Update postojećih + kreiraj nove
        int order = 1;
        for (MaterialDto dto : incoming) {
            // obavezno poravnaj lessonId
            dto.setLessonId(lessonId);

            if (dto.getMaterialId() != null && byId.containsKey(dto.getMaterialId())) {
                // UPDATE postojećeg
                var entity = byId.get(dto.getMaterialId());
                materialMapper.apply(dto, entity);
                entity.setMaterialOrderIndex(
                        dto.getMaterialOrderIndex() != null ? dto.getMaterialOrderIndex() : order
                );
                keepIds.add(entity.getMaterialId());
            } else {
                // CREATE novog
                var entity = materialMapper.toEntity(dto);
                // Ako order nije poslat, dodeli ga po redu
                if (entity.getMaterialOrderIndex() == null) {
                    entity.setMaterialOrderIndex(order);
                }
                lesson.addMaterial(entity); // bi-directional set + orphanRemoval radi brisanja
            }
            order++;
        }

        // 2) Ukloni sve koji nisu u incoming listi (diff)
        // Kopija da izbegnemo ConcurrentModificationException
        var toCheck = new java.util.ArrayList<>(lesson.getMaterials());
        for (var m : toCheck) {
            Long id = m.getMaterialId();
            // Nove entitete još nemaju ID – njih ne diramo
            if (id != null && !keepIds.contains(id)) {
                lesson.removeMaterial(m); // orphanRemoval = true -> obriši iz baze
            }
        }

        // 3) Normalizuj redosled (ako želiš striktno 1..n)
        int idx = 1;
        for (var m : lesson.getMaterials()) {
            m.setMaterialOrderIndex(idx++);
        }

        // Persist kroz Lesson (cascade = ALL)
        lessonRepo.save(lesson);

        // Vrati aktuelnu listu, sortiranu po orderIndex
        return lesson.getMaterials().stream()
                .sorted(java.util.Comparator.comparingInt(m -> m.getMaterialOrderIndex()))
                .map(materialMapper::toDto)
                .collect(java.util.stream.Collectors.toList());
    }

    public long countByCourse(Long courseId) {
        return lessonRepo.countByCourseId(courseId);
    }

    public long countMaterials(Long lessonId) {
        return lessonRepo.countMaterialsByLessonId(lessonId);
    }

}
