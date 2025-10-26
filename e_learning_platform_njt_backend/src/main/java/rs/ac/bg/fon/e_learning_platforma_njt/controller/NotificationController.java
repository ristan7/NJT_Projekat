package rs.ac.bg.fon.e_learning_platforma_njt.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.nio.file.AccessDeniedException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import rs.ac.bg.fon.e_learning_platforma_njt.dto.impl.NotificationDto;
import rs.ac.bg.fon.e_learning_platforma_njt.service.NotificationService;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    // ---------- COLLECTION ----------
    @GetMapping
    @Operation(summary = "Retrieve notifications (filters: userId, unread, limit).")
    public ResponseEntity<List<NotificationDto>> getAll(
            @RequestParam(required = false) Long userId,
            @RequestHeader(name = "X-User-Id", required = false) Long headerUserId,
            @RequestParam(required = false, defaultValue = "false") boolean unread,
            @RequestParam(required = false, defaultValue = "50") int limit) {

        Long effectiveUserId = userId != null ? userId : headerUserId;

        if (effectiveUserId == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "userId is required (query ?userId= or header X-User-Id)."
            );
        }

        return ResponseEntity.ok(service.findAllFiltered(effectiveUserId, unread, limit));
    }

    @GetMapping("/unread/count")
    @Operation(summary = "Count unread notifications for a user (for navbar badge).")
    public ResponseEntity<Long> countUnread(@RequestParam @NotNull Long userId) {
        return ResponseEntity.ok(service.countUnreadByUser(userId));
    }

    // ---------- ITEM ----------
    @GetMapping("/{id}")
    @Operation(summary = "Get notification by id.")
    public ResponseEntity<NotificationDto> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.findById(id));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found: " + id);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Operation(summary = "Create a new notification.")
    @ApiResponse(responseCode = "201", content = {
        @Content(schema = @Schema(implementation = NotificationDto.class), mediaType = "application/json")
    })
    public ResponseEntity<NotificationDto> add(@Valid @RequestBody @NotNull NotificationDto dto) {
        try {
            NotificationDto saved = service.create(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error while saving notification");
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "Update an existing notification.")
    @ApiResponse(responseCode = "200", content = {
        @Content(schema = @Schema(implementation = NotificationDto.class), mediaType = "application/json")
    })
    public ResponseEntity<NotificationDto> update(
            @PathVariable Long id,
            @Valid @RequestBody NotificationDto dto) {
        try {
            dto.setNotificationId(id);
            NotificationDto updated = service.update(dto);
            return ResponseEntity.ok(updated);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error while updating notification");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            service.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (AccessDeniedException ex) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ex.getMessage());
        } catch (RuntimeException ex) {
            // iz servisa: "Notification not found"
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found: " + id);
        }
    }

    // ---------- READ STATE ----------
    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markRead(@PathVariable Long id) {
        try {
            service.markRead(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Integer> markAllRead() {
        try {
            int updated = service.markAllRead(null);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

}


/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
 /*package rs.ac.bg.fon.e_learning_platforma_njt.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import rs.ac.bg.fon.e_learning_platforma_njt.dto.impl.NotificationDto;
import rs.ac.bg.fon.e_learning_platforma_njt.service.NotificationService;

/**
 *
 * @author mikir
 */
 /*
@RestController
@RequestMapping("/api/notification")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    @Operation(summary = "Retreive all Notification entities.")
    public ResponseEntity<List<NotificationDto>> getAll() {
        return new ResponseEntity<>(notificationService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationDto> getById(
            @NotNull(message = "Should not be null or emtpty.")
            @PathVariable(value = "id") Long id) {
        try {
            return new ResponseEntity<>(notificationService.findById(id), HttpStatus.OK);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "NotificationController exception in method findById - not able to find notification with id: " + id);
        }
    }

    @PostMapping
    @Operation(summary = "Create a new Notification entity.")
    public ResponseEntity<NotificationDto> addNotification(@Valid @RequestBody @NotNull NotificationDto notificationDto) {
        try {
            System.out.println(notificationDto);
            NotificationDto saved = notificationService.create(notificationDto);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error while saving notification");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable(value = "id") Long id) {
        try {
            notificationService.deleteById(id);
            return new ResponseEntity<>("Notification successfully deleted", HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>("Notification does not exist: " + id, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing Notification entity.")
    @ApiResponse(responseCode = "200", content = {
        @Content(schema = @Schema(implementation = NotificationDto.class), mediaType = "aplication/json")
    })
    public ResponseEntity<NotificationDto> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody NotificationDto notificationDto) {
        try {
            notificationDto.setNotificationId(id); //Osiguravamo da se azurira pravi entitet
            NotificationDto updated = notificationService.update(notificationDto);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error while updating notification");
        }
    }

 */
