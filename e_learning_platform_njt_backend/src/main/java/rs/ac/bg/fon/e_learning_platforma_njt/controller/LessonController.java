package rs.ac.bg.fon.e_learning_platforma_njt.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;

import rs.ac.bg.fon.e_learning_platforma_njt.dto.impl.LessonDto;
import rs.ac.bg.fon.e_learning_platforma_njt.dto.impl.MaterialDto;
import rs.ac.bg.fon.e_learning_platforma_njt.service.LessonService;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api")
public class LessonController {

    private final LessonService lessonService;

    // TODO: zameni realnim načinom dobijanja korisnika/uloge (SecurityContext/JWT)
    private Long getRequesterId() {
        return 1L;
    }

    private String getRoleName() {
        return "TEACHER";
    } // "STUDENT" | "TEACHER" | "ADMIN"

    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    /* ===========================================================
       ======================== LESSON READ =======================
       =========================================================== */
    @PreAuthorize("hasAnyRole('STUDENT','TEACHER','ADMIN')")
    @GetMapping("/courses/{courseId}/lessons")
    public ResponseEntity<List<LessonDto>> getLessonsByCourse(@PathVariable Long courseId) throws Exception {
        Long requesterId = getRequesterId();
        String roleName = getRoleName();
        List<LessonDto> result = lessonService.findAllByCourse(courseId, requesterId, roleName);
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasAnyRole('STUDENT','TEACHER','ADMIN')")
    @GetMapping("/lessons/{lessonId}")
    public ResponseEntity<LessonDto> getLesson(@PathVariable Long lessonId) throws Exception {
        Long requesterId = getRequesterId();
        String roleName = getRoleName();
        LessonDto dto = lessonService.findById(lessonId, requesterId, roleName);
        return ResponseEntity.ok(dto);
    }

    /* ===========================================================
       ======================== LESSON WRITE ======================
       =========================================================== */
    // TEACHER ONLY
    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/courses/{courseId}/lessons")
    public ResponseEntity<LessonDto> createLesson(@PathVariable Long courseId,
            @Valid @RequestBody LessonDto dto) throws Exception {
        Long teacherId = getRequesterId();
        LessonDto created = lessonService.create(courseId, dto, teacherId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // TEACHER ONLY
    @PreAuthorize("hasRole('TEACHER')")
    @PutMapping("/lessons/{lessonId}")
    public ResponseEntity<LessonDto> updateLesson(@PathVariable Long lessonId,
            @Valid @RequestBody LessonDto dto) throws Exception {
        Long teacherId = getRequesterId();
        LessonDto updated = lessonService.update(lessonId, dto, teacherId);
        return ResponseEntity.ok(updated);
    }

    // TEACHER ONLY – PATCH dostupnosti
    @PreAuthorize("hasRole('TEACHER')")
    @PatchMapping("/lessons/{lessonId}/available")
    public ResponseEntity<LessonDto> patchLessonAvailability(@PathVariable Long lessonId,
            @RequestParam("value") boolean available) throws Exception {
        Long teacherId = getRequesterId();
        LessonDto updated = lessonService.patchAvailability(lessonId, available, teacherId);
        return ResponseEntity.ok(updated);
    }

    // TEACHER ONLY – PATCH tipa lekcije (lookup)
    @PreAuthorize("hasRole('TEACHER')")
    @PatchMapping("/lessons/{lessonId}/type")
    public ResponseEntity<LessonDto> patchLessonType(@PathVariable Long lessonId,
            @RequestParam("lessonTypeId") Long lessonTypeId) throws Exception {
        Long teacherId = getRequesterId();
        LessonDto updated = lessonService.patchType(lessonId, lessonTypeId, teacherId);
        return ResponseEntity.ok(updated);
    }

    // TEACHER ONLY – DELETE
    @PreAuthorize("hasRole('TEACHER')")
    @DeleteMapping("/lessons/{lessonId}")
    public ResponseEntity<Void> deleteLesson(@PathVariable Long lessonId) throws Exception {
        Long teacherId = getRequesterId();
        lessonService.delete(lessonId, teacherId);
        return ResponseEntity.noContent().build();
    }

    /* ===========================================================
       ============== MATERIALS (WRITE preko Lesson) ==============
       =========================================================== */
    // READ (Student vidi samo ako je platio; Teacher/Admin uvek)
    @PreAuthorize("hasAnyRole('STUDENT','TEACHER','ADMIN')")
    @GetMapping("/lessons/{lessonId}/materials")
    public ResponseEntity<List<MaterialDto>> getMaterialsByLesson(@PathVariable Long lessonId) throws Exception {
        Long requesterId = getRequesterId();
        String roleName = getRoleName();
        List<MaterialDto> list = lessonService.findMaterialsByLesson(lessonId, requesterId, roleName);
        return ResponseEntity.ok(list);
    }

    // TEACHER ONLY – ADD
    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/lessons/{lessonId}/materials")
    public ResponseEntity<MaterialDto> addMaterial(@PathVariable Long lessonId,
            @Valid @RequestBody MaterialDto dto) throws Exception {
        Long teacherId = getRequesterId();
        MaterialDto created = lessonService.addMaterial(lessonId, dto, teacherId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // TEACHER ONLY – UPDATE
    @PreAuthorize("hasRole('TEACHER')")
    @PutMapping("/lessons/{lessonId}/materials/{materialId}")
    public ResponseEntity<MaterialDto> updateMaterial(@PathVariable Long lessonId,
            @PathVariable Long materialId,
            @Valid @RequestBody MaterialDto dto) throws Exception {
        Long teacherId = getRequesterId();
        dto.setMaterialId(materialId);
        MaterialDto updated = lessonService.updateMaterial(lessonId, dto, teacherId);
        return ResponseEntity.ok(updated);
    }

    // TEACHER ONLY – DELETE
    @PreAuthorize("hasRole('TEACHER')")
    @DeleteMapping("/lessons/{lessonId}/materials/{materialId}")
    public ResponseEntity<Void> removeMaterial(@PathVariable Long lessonId,
            @PathVariable Long materialId) throws Exception {
        Long teacherId = getRequesterId();
        lessonService.removeMaterial(lessonId, materialId, teacherId);
        return ResponseEntity.noContent().build();
    }
}
