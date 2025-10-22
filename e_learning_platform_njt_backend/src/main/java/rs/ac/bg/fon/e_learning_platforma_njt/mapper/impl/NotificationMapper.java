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

        Long notificationTypeId = (e.getType() != null) ? e.getType().getNotificationTypeId() : null;
        Long userId = (e.getUser() != null) ? e.getUser().getUserId() : null;

        NotificationDto dto = new NotificationDto(
                e.getNotificationId(),
                e.getNotificationTitle(),
                e.getMessage(),
                e.getSentAt(),
                /* read: */ e.isRead(),
                notificationTypeId,
                userId
        );
        return dto;
    }

    @Override
    public Notification toEntity(NotificationDto t) {
        if (t == null) {
            return null;
        }

        NotificationType notificationType
                = (t.getNotificationTypeId() != null) ? new NotificationType(t.getNotificationTypeId()) : null;
        User user
                = (t.getUserId() != null) ? new User(t.getUserId()) : null;

        Notification e = new Notification(
                t.getNotificationId(),
                t.getNotificationTitle(),
                t.getMessage(),
                /* sentAt = */ null, // @PrePersist će postaviti ako je null
                notificationType,
                user
        );

        // read NE postavljamo iz DTO-a (read-only sa fronta)
        // e.setRead(t.isRead());  // namerno NE
        return e;
    }

    /**
     * Primeni izmene DTO-a na postojeći entitet (update).
     */
    public void apply(NotificationDto t, Notification e) {
        if (t == null || e == null) {
            return;
        }

        e.setNotificationTitle(t.getNotificationTitle());
        e.setMessage(t.getMessage());
        e.setType(t.getNotificationTypeId() != null ? new NotificationType(t.getNotificationTypeId()) : null);
        e.setUser(t.getUserId() != null ? new User(t.getUserId()) : null);

        // sentAt i read ne diramo ovde:
        // - sentAt ostaje iz prvobitnog nastanka
        // - read menja isključivo servis (markRead / markAllRead)
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
 /*
package rs.ac.bg.fon.e_learning_platforma_njt.mapper.impl;

import org.springframework.stereotype.Component;
import rs.ac.bg.fon.e_learning_platforma_njt.dto.impl.NotificationDto;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.impl.Notification;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.impl.User;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.lookup.NotificationType;
import rs.ac.bg.fon.e_learning_platforma_njt.mapper.DtoEntityMapper;

/**
 *
 * @author mikir
 */
 /*
@Component
public class NotificationMapper implements DtoEntityMapper<NotificationDto, Notification> {

    @Override
    public NotificationDto toDto(Notification e) {
        Long notificationTypeId = e.getType() != null ? e.getType().getNotificationTypeId() : null;
        Long userId = e.getUser() != null ? e.getUser().getUserId() : null;
        return new NotificationDto(
                e.getNotificationId(),
                e.getNotificationTitle(),
                e.getMessage(),
                e.getSentAt(),
                notificationTypeId,
                userId
        );
    }

    @Override
    public Notification toEntity(NotificationDto t) {
        NotificationType notificationType = t.getNotificationTypeId() != null ? new NotificationType(t.getNotificationTypeId()) : null;
        User user = t.getUserId() != null ? new User(t.getUserId()) : null;
        return new Notification(
                t.getNotificationId(),
                t.getNotificationTitle(),
                t.getMessage(),
                null,
                notificationType,
                user
        );
    }

    //UPDATE
    public void apply(NotificationDto t, Notification e) {
        e.setNotificationTitle(t.getNotificationTitle());
        e.setMessage(t.getMessage());
        e.setType(t.getNotificationTypeId() != null ? new NotificationType(t.getNotificationTypeId()) : null);
        e.setUser(t.getUserId() != null ? new User(t.getUserId()) : null);
        // sentAt se ne dira ostaje isti
    }

}
 */
