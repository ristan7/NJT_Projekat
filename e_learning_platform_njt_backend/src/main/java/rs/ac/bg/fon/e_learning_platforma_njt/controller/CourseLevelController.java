// CourseLevelController.java
package rs.ac.bg.fon.e_learning_platforma_njt.controller;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.lookup.CourseLevel;

import java.util.List;

@RestController
@RequestMapping("/api/course-levels")
@CrossOrigin(origins = "http://localhost:3000")
public class CourseLevelController {

    @PersistenceContext
    private EntityManager em;

    @GetMapping
    public ResponseEntity<List<CourseLevel>> listAll() {
        List<CourseLevel> list = em.createQuery(
                "SELECT cl FROM CourseLevel cl ORDER BY cl.courseLevelName", CourseLevel.class
        ).getResultList();
        return ResponseEntity.ok(list);
    }

}
