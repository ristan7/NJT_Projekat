package rs.ac.bg.fon.e_learning_platforma_njt.controller;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.lookup.NotificationType;

import java.util.List;

@RestController
@RequestMapping("/api/notification-types")
@CrossOrigin(origins = "http://localhost:3000")
public class NotificationTypeController {

    @PersistenceContext
    private EntityManager em;

    @GetMapping
    public ResponseEntity<List<NotificationType>> listAll() {
        List<NotificationType> list = em
                .createQuery("SELECT nt FROM NotificationType nt ORDER BY nt.notificationTypeName", NotificationType.class)
                .getResultList();
        return ResponseEntity.ok(list);
    }
}
