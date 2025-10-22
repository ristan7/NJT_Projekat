package rs.ac.bg.fon.e_learning_platforma_njt.service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import rs.ac.bg.fon.e_learning_platforma_njt.dto.impl.NotificationDto;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.impl.Notification;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.impl.User;
import rs.ac.bg.fon.e_learning_platforma_njt.mapper.impl.NotificationMapper;
import rs.ac.bg.fon.e_learning_platforma_njt.repository.impl.NotificationRepository;
import rs.ac.bg.fon.e_learning_platforma_njt.repository.impl.UserRepository;

@Service
public class NotificationService {

    private final NotificationRepository repo;
    private final NotificationMapper mapper;
    private final UserRepository users; // <-- NEW

    public NotificationService(
            NotificationRepository repo,
            NotificationMapper mapper,
            UserRepository users // <-- NEW
    ) {
        this.repo = repo;
        this.mapper = mapper;
        this.users = users; // <-- NEW
    }

    /* ---------- Basic CRUD ---------- */
    public List<NotificationDto> findAll() {
        return repo.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    public NotificationDto findById(Long id) throws Exception {
        return mapper.toDto(repo.findById(id));
    }

    /**
     * Samo ADMIN može da kreira; dodatno: ne možeš poslati notifikaciju sam sebi.
     */
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public NotificationDto create(NotificationDto dto) throws Exception {
        // Uzmemo aktuelnog korisnika iz SecurityContext-a
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new Exception("Unauthenticated");
        }

        User sender = users.findByUsername(auth.getName());
        if (sender == null) {
            throw new Exception("Authenticated user not found.");
        }

        // Izvuci targetId iz DTO-a (podržavamo više oblika)
        Long targetId = null;
        try {
            // ako NotificationDto ima getUserId()
            targetId = (Long) NotificationDto.class.getMethod("getUserId").invoke(dto);
        } catch (ReflectiveOperationException ignore) {
            /* nije obavezno */ }

        if (targetId == null) {
            try {
                // ili ako ima ugnježdenog user-a: dto.getUser().getUserId()
                Object uObj = NotificationDto.class.getMethod("getUser").invoke(dto);
                if (uObj != null) {
                    targetId = (Long) uObj.getClass().getMethod("getUserId").invoke(uObj);
                }
            } catch (ReflectiveOperationException ignore) {
                /* nije obavezno */ }
        }

        if (targetId != null && sender.getUserId() != null && targetId.equals(sender.getUserId())) {
            throw new IllegalArgumentException("You cannot send a notification to yourself.");
        }

        Notification entity = mapper.toEntity(dto);
        repo.save(entity);
        return mapper.toDto(entity);
    }

    @Transactional
    public NotificationDto update(NotificationDto dto) throws Exception {
        Notification existing = repo.findById(dto.getNotificationId());
        // sentAt i read se ne diraju u standardnom update-u
        mapper.apply(dto, existing);
        repo.save(existing);
        return mapper.toDto(existing);
    }

    @Transactional
    public void deleteById(Long id) throws AccessDeniedException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new AccessDeniedException("Unauthenticated");
        }

        // ko je ulogovan
        User me = users.findByUsername(auth.getName());
        if (me == null || me.getUserId() == null) {
            throw new AccessDeniedException("User not found");
        }

        try {
            // uzmi notifikaciju
            Notification n = repo.findById(id);

            // dozvoli samo vlasniku
            Long ownerId = (n.getUser() != null) ? n.getUser().getUserId() : null;
            if (ownerId == null || !ownerId.equals(me.getUserId())) {
                throw new AccessDeniedException("You can delete only your own notifications.");
            }

            repo.deleteById(id);
        } catch (AccessDeniedException e) {
            throw e; // ide 403
        } catch (Exception e) {
            // repo.findById baca Exception ako ne postoji
            throw new RuntimeException("Notification not found", e);
        }
    }

    /* ---------- Za front: filter, unread, mark read ---------- */
    /**
     * Filter po userId + (opciono) samo nepročitane + limit, sortirano DESC po datumu. Ako userId == null, vraća sve.
     */
    public List<NotificationDto> findAllFiltered(Long userId, boolean unreadOnly, int limit) {
        int safeLimit = Math.max(0, limit);
        List<Notification> list = (userId != null)
                ? repo.findAllByUserFiltered(userId, unreadOnly, safeLimit)
                : repo.findAll();
        return list.stream().map(mapper::toDto).collect(Collectors.toList());
    }

    /**
     * Broj nepročitanih za badge (read = false).
     */
    public long countUnreadByUser(Long userId) {
        if (userId == null) {
            return 0L;
        }
        return repo.countUnreadByUser(userId);
    }

    /**
     * Označi jednu notifikaciju kao pročitanu.
     */
    @Transactional
    public void markRead(Long id) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new Exception("Unauthenticated");
        }

        User me = users.findByUsername(auth.getName());
        if (me == null) {
            throw new Exception("User not found");
        }

        int updated = repo.markReadForUser(id, me.getUserId());
        if (updated == 0) {
            throw new Exception("Notification not found or not owned by user");
        }
    }

    @Transactional
    public int markAllRead(Long userId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            return 0;
        }
        User me = users.findByUsername(auth.getName());
        return repo.markAllReadForUser(me.getUserId());
    }

}
