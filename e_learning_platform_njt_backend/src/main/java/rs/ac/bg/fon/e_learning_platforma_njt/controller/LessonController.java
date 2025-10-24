package rs.ac.bg.fon.e_learning_platforma_njt.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import rs.ac.bg.fon.e_learning_platforma_njt.dto.impl.LessonDto;
import rs.ac.bg.fon.e_learning_platforma_njt.dto.impl.MaterialDto;
import rs.ac.bg.fon.e_learning_platforma_njt.security.JwtService;
import rs.ac.bg.fon.e_learning_platforma_njt.service.LessonService;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api")
public class LessonController {

    private final LessonService lessonService;
    private final JwtService jwtService;

    public LessonController(LessonService lessonService, JwtService jwtService) {
        this.lessonService = lessonService;
        this.jwtService = jwtService;
    }

    /* ======================= Helpers (JWT/role) ======================= */
    private Long getRequesterId() {
        String token = jwtService.extractTokenFromHeader();
        return jwtService.requireUserId(token); // baca 401 ako nema/nevaži
    }

    private String getRoleName() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            for (GrantedAuthority ga : auth.getAuthorities()) {
                String r = ga.getAuthority(); // npr. ROLE_TEACHER
                if (r != null) {
                    return r.startsWith("ROLE_") ? r.substring(5) : r;
                }
            }
        }
        String token = jwtService.extractTokenFromHeader();
        String claimRole = jwtService.extractRoleName(token);
        return claimRole != null ? claimRole : "ANON"; // STUDENT | TEACHER | ADMIN
    }

    /* =========================== LESSON READ =========================== */
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

    /* =========================== LESSON WRITE ========================== */
    // TEACHER ONLY
    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/courses/{courseId}/lessons")
    public ResponseEntity<LessonDto> createLesson(@PathVariable Long courseId,
            @Valid @RequestBody LessonDto dto) throws Exception {
        Long teacherId = getRequesterId();
        // samo postavi courseId; orderIndex će servis odraditi ako je null
        dto.setCourseId(courseId);
        LessonDto created = lessonService.create(courseId, dto, teacherId);
        return ResponseEntity.status(201).body(created);
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

    /* ============== MATERIALS (READ preko LessonService) ============== */
    // READ (Student vidi samo ako ima ACTIVE enrollment; Teacher/Admin uvek)
    @PreAuthorize("hasAnyRole('STUDENT','TEACHER','ADMIN')")
    @GetMapping("/lessons/{lessonId}/materials")
    public ResponseEntity<List<MaterialDto>> getMaterialsByLesson(@PathVariable Long lessonId) throws Exception {
        Long requesterId = getRequesterId();
        String roleName = getRoleName();
        List<MaterialDto> list = lessonService.findMaterialsByLesson(lessonId, requesterId, roleName);
        return ResponseEntity.ok(list);
    }

    // TEACHER ONLY – PATCH freePreview (preporučeni dodatak)
    @PreAuthorize("hasRole('TEACHER')")
    @PatchMapping("/lessons/{lessonId}/preview")
    public ResponseEntity<LessonDto> patchLessonPreview(@PathVariable Long lessonId,
            @RequestParam("value") boolean freePreview) throws Exception {
        Long teacherId = getRequesterId();
        // reuse update: minimalan hop preko DTO-a
        LessonDto dto = new LessonDto();
        dto.setFreePreview(freePreview);
        LessonDto updated = lessonService.update(lessonId, dto, teacherId);
        return ResponseEntity.ok(updated);
    }

    // TEACHER ONLY – B U L K  upsert/replace materijala
    @PreAuthorize("hasRole('TEACHER')")
    @PutMapping("/lessons/{lessonId}/materials")
    public ResponseEntity<List<MaterialDto>> replaceMaterials(
            @PathVariable Long lessonId,
            @Valid @RequestBody List<MaterialDto> payload) throws Exception {

        Long teacherId = getRequesterId();
        // (Opcionalno) osiguraj da svi item-i ciljaju isti lesson:
        for (MaterialDto m : payload) {
            m.setLessonId(lessonId);
        }
        List<MaterialDto> saved = lessonService.replaceMaterials(lessonId, payload, teacherId);
        return ResponseEntity.ok(saved);
    }

    // LessonController.java
    @PreAuthorize("hasAnyRole('STUDENT','TEACHER','ADMIN')")
    @GetMapping("/courses/{courseId}/lessons/count")
    public ResponseEntity<Long> getLessonCount(@PathVariable Long courseId) {
        long cnt = lessonService.countByCourse(courseId);
        return ResponseEntity.ok(cnt);
    }

    @PreAuthorize("hasAnyRole('STUDENT','TEACHER','ADMIN')")
    @GetMapping("/lessons/{lessonId}/materials/count")
    public ResponseEntity<Long> getMaterialsCount(@PathVariable Long lessonId) throws Exception {
        Long requesterId = getRequesterId();
        String roleName = getRoleName();

        // Reuse pristupnu logiku: ako sme da vidi materijale, sme i count
        lessonService.findById(lessonId, requesterId, roleName); // baciće 403 ako ne sme
        long cnt = lessonService.countMaterials(lessonId);
        return ResponseEntity.ok(cnt);
    }

}
