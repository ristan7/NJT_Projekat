package rs.ac.bg.fon.e_learning_platforma_njt.controller;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.lookup.EnrollmentStatus;
import java.util.List;

@RestController
@RequestMapping("/api/enrollment-statuses")
@CrossOrigin(origins = "http://localhost:3000")
public class EnrollmentStatusController {

    @PersistenceContext
    private EntityManager em;

    @GetMapping
    public ResponseEntity<List<EnrollmentStatus>> listAll() {
        List<EnrollmentStatus> list = em.createQuery(
                "SELECT es FROM EnrollmentStatus es ORDER BY es.enrollmentStatusName",
                EnrollmentStatus.class
        ).getResultList();
        return ResponseEntity.ok(list);
    }
}
