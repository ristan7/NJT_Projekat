package rs.ac.bg.fon.e_learning_platforma_njt.mapper.impl;

import org.springframework.stereotype.Component;
import rs.ac.bg.fon.e_learning_platforma_njt.dto.impl.NotificationDto;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.impl.Notification;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.impl.User;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.lookup.NotificationType;
import rs.ac.bg.fon.e_learning_platforma_njt.mapper.DtoEntityMapper;

@Component
public class NotificationMapper implements DtoEntityMapper<NotificationDto, Notification> {

    @Override
    public NotificationDto toDto(Notification e) {
        if (e == null) {
            return null;
        }

        Long typeId = (e.getType() != null) ? e.getType().getNotificationTypeId() : null;
        String typeName = (e.getType() != null) ? e.getType().getNotificationTypeName() : null; // prilagodi geteru u entitetu
        Long userId = (e.getUser() != null) ? e.getUser().getUserId() : null;

        NotificationDto dto = new NotificationDto();
        dto.setNotificationId(e.getNotificationId());
        dto.setNotificationTitle(e.getNotificationTitle());
        dto.setMessage(e.getMessage());
        dto.setNotificationTypeId(typeId);
        dto.setUserId(userId);
        dto.setRead(e.isRead());          // READ_ONLY → samo izlaz
        dto.setSentAt(e.getSentAt());     // READ_ONLY → samo izlaz
        dto.setNotificationTypeName(typeName); // READ_ONLY → samo izlaz
        return dto;
    }

    @Override
    public Notification toEntity(NotificationDto t) {
        if (t == null) {
            return null;
        }

        Notification e = new Notification();
        e.setNotificationId(t.getNotificationId()); // null za create
        if (t.getNotificationTitle() != null) {
            e.setNotificationTitle(safeTrim(t.getNotificationTitle()));
        }
        if (t.getMessage() != null) {
            e.setMessage(t.getMessage());
        }
        if (t.getNotificationTypeId() != null) {
            e.setType(new NotificationType(t.getNotificationTypeId()));
        }
        if (t.getUserId() != null) {
            e.setUser(new User(t.getUserId()));
        }
        // read i sentAt NE postavljamo iz DTO-a:
        // - read menja servis (markRead/markAllRead)
        // - sentAt postavlja @PrePersist (ako je null)
        return e;
    }

    /**
     * Partial UPDATE: - Ne diramo read/sentAt (servis/@PrePersist). - Menjamo type/user samo ako su prosleđeni ID-jevi (dozvoljeno ako je to tvoja poslovna logika).
     */
    @Override
    public void apply(NotificationDto t, Notification e) {
        if (t == null || e == null) {
            return;
        }

        if (t.getNotificationTitle() != null) {
            e.setNotificationTitle(safeTrim(t.getNotificationTitle()));
        }
        if (t.getMessage() != null) {
            e.setMessage(t.getMessage());
        }
        if (t.getNotificationTypeId() != null) {
            e.setType(new NotificationType(t.getNotificationTypeId()));
        }
        if (t.getUserId() != null) {
            e.setUser(new User(t.getUserId()));
        }
        // read/sentAt se ne diraju ovde
    }

    private String safeTrim(String s) {
        return (s == null) ? null : s.trim();
    }
}
