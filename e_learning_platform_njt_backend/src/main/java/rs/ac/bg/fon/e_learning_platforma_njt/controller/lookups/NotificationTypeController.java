package rs.ac.bg.fon.e_learning_platforma_njt.controller.lookups;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import rs.ac.bg.fon.e_learning_platforma_njt.service.lookups.NotificationTypeService;

import java.util.List;
import rs.ac.bg.fon.e_learning_platforma_njt.dto.impl.lookups.NotificationTypeDto;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/notification-types")
public class NotificationTypeController {

    private final NotificationTypeService service;

    public NotificationTypeController(NotificationTypeService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "List all notification types (id + name).")
    public ResponseEntity<List<NotificationTypeDto>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }
    
}
