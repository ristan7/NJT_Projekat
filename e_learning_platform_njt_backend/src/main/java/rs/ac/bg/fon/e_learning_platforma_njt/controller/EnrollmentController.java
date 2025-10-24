package rs.ac.bg.fon.e_learning_platforma_njt.controller;

import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import rs.ac.bg.fon.e_learning_platforma_njt.dto.impl.EnrollmentDto;
import rs.ac.bg.fon.e_learning_platforma_njt.security.JwtService;
import rs.ac.bg.fon.e_learning_platforma_njt.service.EnrollmentService;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final JwtService jwtService;

    public EnrollmentController(EnrollmentService enrollmentService, JwtService jwtService) {
        this.enrollmentService = enrollmentService;
        this.jwtService = jwtService;
    }

    private Long getRequesterId() {
        String token = jwtService.extractTokenFromHeader();
        return jwtService.requireUserId(token);
    }

    private String getRoleName() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            for (GrantedAuthority ga : auth.getAuthorities()) {
                String r = ga.getAuthority();
                if (r != null) {
                    return r.startsWith("ROLE_") ? r.substring(5) : r;
                }
            }
        }
        String token = jwtService.extractTokenFromHeader();
        String claimRole = jwtService.extractRoleName(token);
        return claimRole != null ? claimRole : "ANON";
    }

    /* =============================== READ =============================== */
    // Detalj upisa — student (ako je njegov), teacher (ako je njegov kurs), admin (uvek)
    @PreAuthorize("hasAnyRole('STUDENT','TEACHER','ADMIN')")
    @GetMapping("/enrollments/{enrollmentId}")
    public ResponseEntity<EnrollmentDto> byId(@PathVariable Long enrollmentId) throws Exception {
        Long requesterId = getRequesterId();
        String roleName = getRoleName();
        return ResponseEntity.ok(enrollmentService.findById(enrollmentId, requesterId, roleName));
    }

    // Lista upisa za STUDENTA (self ili admin), sa filtrima i paginacijom
    @PreAuthorize("hasAnyRole('STUDENT','ADMIN')")
    @GetMapping("/students/{studentId}/enrollments")
    public ResponseEntity<List<EnrollmentDto>> listForStudent(
            @PathVariable Long studentId,
            @RequestParam(required = false) Long statusId,
            @RequestParam(defaultValue = "0") @Min(0) int offset,
            @RequestParam(defaultValue = "50") @Min(1) int limit
    ) throws Exception {
        Long requesterId = getRequesterId();
        String roleName = getRoleName();
        return ResponseEntity.ok(
                enrollmentService.listForStudent(studentId, statusId, offset, limit, requesterId, roleName)
        );
    }

    // Lista upisa za TEACHERA (samo svoje) ili admina, sa filtrima i paginacijom
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    @GetMapping("/teachers/{teacherId}/enrollments")
    public ResponseEntity<List<EnrollmentDto>> listForTeacher(
            @PathVariable Long teacherId,
            @RequestParam(required = false) Long statusId,
            @RequestParam(defaultValue = "0") @Min(0) int offset,
            @RequestParam(defaultValue = "50") @Min(1) int limit
    ) throws Exception {
        Long requesterId = getRequesterId();
        String roleName = getRoleName();
        return ResponseEntity.ok(
                enrollmentService.listForTeacher(teacherId, statusId, offset, limit, requesterId, roleName)
        );
    }

    // Lista upisa po KURSU — teacher mora biti autor tog kursa, admin uvek može
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    @GetMapping("/courses/{courseId}/enrollments")
    public ResponseEntity<List<EnrollmentDto>> listByCourse(@PathVariable Long courseId) throws Exception {
        Long requesterId = getRequesterId();
        String roleName = getRoleName();
        return ResponseEntity.ok(enrollmentService.listByCourse(courseId, requesterId, roleName));
    }

    /* =============================== WRITE ============================== */
    // STUDENT podnosi zahtev za upis na kurs (REQUESTED). studentId = requesterId.
    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/courses/{courseId}/enrollments")
    public ResponseEntity<EnrollmentDto> requestEnrollment(@PathVariable Long courseId) throws Exception {
        Long studentId = getRequesterId();
        String roleName = "STUDENT";
        EnrollmentDto created = enrollmentService.requestEnrollment(courseId, studentId, roleName);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /* ============================ STATUS AKCIJE ========================== */
    // Aktivacija (ACTIVE) — teacher svog kursa ili admin
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    @PostMapping("/enrollments/{enrollmentId}/activate")
    public ResponseEntity<EnrollmentDto> activate(@PathVariable Long enrollmentId) throws Exception {
        Long actorId = getRequesterId();
        String roleName = getRoleName();
        return ResponseEntity.ok(enrollmentService.activate(enrollmentId, actorId, roleName));
    }

    // Završavanje (COMPLETED) — teacher svog kursa ili admin
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    @PostMapping("/enrollments/{enrollmentId}/complete")
    public ResponseEntity<EnrollmentDto> complete(@PathVariable Long enrollmentId) throws Exception {
        Long actorId = getRequesterId();
        String roleName = getRoleName();
        return ResponseEntity.ok(enrollmentService.complete(enrollmentId, actorId, roleName));
    }

    // Otkazivanje (CANCELLED) — student (sopstveni) ili teacher/admin nad sopstvenim kursom
    @PreAuthorize("hasAnyRole('STUDENT','TEACHER','ADMIN')")
    @PostMapping("/enrollments/{enrollmentId}/cancel")
    public ResponseEntity<EnrollmentDto> cancel(@PathVariable Long enrollmentId) throws Exception {
        Long actorId = getRequesterId();
        String roleName = getRoleName();
        return ResponseEntity.ok(enrollmentService.cancel(enrollmentId, actorId, roleName));
    }
}
