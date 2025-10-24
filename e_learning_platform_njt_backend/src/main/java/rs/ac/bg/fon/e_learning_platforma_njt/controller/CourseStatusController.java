// CourseStatusController.java
package rs.ac.bg.fon.e_learning_platforma_njt.controller;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.lookup.CourseStatus;

import java.util.List;

@RestController
@RequestMapping("/api/course-statuses")
@CrossOrigin(origins = "http://localhost:3000")
public class CourseStatusController {

    @PersistenceContext
    private EntityManager em;

    @GetMapping
    public ResponseEntity<List<CourseStatus>> listAll() {
        List<CourseStatus> list = em.createQuery(
                "SELECT cs FROM CourseStatus cs ORDER BY cs.courseStatusName",
                CourseStatus.class
        ).getResultList();
        return ResponseEntity.ok(list);
    }
}
