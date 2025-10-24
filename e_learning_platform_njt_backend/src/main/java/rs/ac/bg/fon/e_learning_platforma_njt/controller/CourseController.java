package rs.ac.bg.fon.e_learning_platforma_njt.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import rs.ac.bg.fon.e_learning_platforma_njt.dto.impl.CourseDto;
import rs.ac.bg.fon.e_learning_platforma_njt.security.JwtService;
import rs.ac.bg.fon.e_learning_platforma_njt.service.CourseService;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;
    private final JwtService jwtService;

    public CourseController(CourseService courseService, JwtService jwtService) {
        this.courseService = courseService;
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
                String r = ga.getAuthority(); // npr. ROLE_TEACHER
                if (r != null) {
                    return r.startsWith("ROLE_") ? r.substring(5) : r;
                }
            }
        }
        String token = jwtService.extractTokenFromHeader();
        String claimRole = jwtService.extractRoleName(token);
        return claimRole != null ? claimRole : "ANON";
    }

    /* ============================ READ ============================ */
    @PreAuthorize("hasAnyRole('STUDENT','TEACHER','ADMIN')")
    @GetMapping
    public ResponseEntity<List<CourseDto>> all() {
        return ResponseEntity.ok(courseService.findAll());
    }

    @PreAuthorize("hasAnyRole('STUDENT','TEACHER','ADMIN')")
    @GetMapping("/{courseId}")
    public ResponseEntity<CourseDto> byId(@PathVariable Long courseId) throws Exception {
        return ResponseEntity.ok(courseService.findById(courseId));
    }

    // Teacher dashboard (sopstveni kursevi)
    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<CourseDto>> byAuthor(@PathVariable Long authorId) {
        return ResponseEntity.ok(courseService.findAllByAuthor(authorId));
    }

    // Jednostavna pretraga po naslovu
    @PreAuthorize("hasAnyRole('STUDENT','TEACHER','ADMIN')")
    @GetMapping("/search")
    public ResponseEntity<List<CourseDto>> search(@RequestParam(name = "q", required = false) String q) {
        return ResponseEntity.ok(courseService.searchByTitle(q));
    }

    /* ============================ WRITE (TEACHER) ============================ */
    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping
    public ResponseEntity<CourseDto> create(@Valid @RequestBody CourseDto dto) throws IllegalAccessException {
        Long requesterId = getRequesterId();
        String roleName = getRoleName();
        CourseDto created = courseService.create(dto, requesterId, roleName);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PreAuthorize("hasRole('TEACHER')")
    @PutMapping("/{courseId}")
    public ResponseEntity<CourseDto> update(@PathVariable Long courseId,
            @Valid @RequestBody CourseDto dto) throws Exception {
        Long requesterId = getRequesterId();
        String roleName = getRoleName();
        CourseDto updated = courseService.update(courseId, dto, requesterId, roleName);
        return ResponseEntity.ok(updated);
    }

    @PreAuthorize("hasRole('TEACHER')")
    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> delete(@PathVariable Long courseId) throws Exception {
        Long requesterId = getRequesterId();
        String roleName = getRoleName();
        courseService.delete(courseId, requesterId, roleName);
        return ResponseEntity.noContent().build();
    }

    /* ============================ PATCH (TEACHER) ============================ */
    @PreAuthorize("hasRole('TEACHER')")
    @PatchMapping("/{courseId}/title")
    public ResponseEntity<CourseDto> patchTitle(@PathVariable Long courseId, @RequestParam String value) throws Exception {
        Long requesterId = getRequesterId();
        String roleName = getRoleName();
        return ResponseEntity.ok(courseService.patchTitle(courseId, value, requesterId, roleName));
    }

    @PreAuthorize("hasRole('TEACHER')")
    @PatchMapping("/{courseId}/description")
    public ResponseEntity<CourseDto> patchDescription(@PathVariable Long courseId, @RequestParam String value) throws Exception {
        Long requesterId = getRequesterId();
        String roleName = getRoleName();
        return ResponseEntity.ok(courseService.patchDescription(courseId, value, requesterId, roleName));
    }

    @PreAuthorize("hasRole('TEACHER')")
    @PatchMapping("/{courseId}/level")
    public ResponseEntity<CourseDto> patchLevel(@PathVariable Long courseId, @RequestParam Long levelId) throws Exception {
        Long requesterId = getRequesterId();
        String roleName = getRoleName();
        return ResponseEntity.ok(courseService.patchLevel(courseId, levelId, requesterId, roleName));
    }

    @PreAuthorize("hasRole('TEACHER')")
    @PatchMapping("/{courseId}/status")
    public ResponseEntity<CourseDto> patchStatus(@PathVariable Long courseId, @RequestParam Long statusId) throws Exception {
        Long requesterId = getRequesterId();
        String roleName = getRoleName();
        return ResponseEntity.ok(courseService.patchStatus(courseId, statusId, requesterId, roleName));
    }
}
