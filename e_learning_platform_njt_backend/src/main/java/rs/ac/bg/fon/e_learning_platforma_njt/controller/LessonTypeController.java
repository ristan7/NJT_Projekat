// LessonTypeController.java
package rs.ac.bg.fon.e_learning_platforma_njt.controller;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.lookup.LessonType;

import java.util.List;

@RestController
@RequestMapping("/api/lesson-types")
@CrossOrigin(origins = "http://localhost:3000")
public class LessonTypeController {

    @PersistenceContext
    private EntityManager em;

    @GetMapping
    public ResponseEntity<List<LessonType>> listAll() {
        List<LessonType> list = em.createQuery(
                "SELECT lt FROM LessonType lt ORDER BY lt.lessonTypeName", LessonType.class
        ).getResultList();
        return ResponseEntity.ok(list);
    }
}
