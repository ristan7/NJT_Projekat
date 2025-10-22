package rs.ac.bg.fon.e_learning_platforma_njt.entity.impl;

import rs.ac.bg.fon.e_learning_platforma_njt.entity.lookup.NotificationType;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.MyEntity;

@Entity
@Table(
        name = "notification",
        indexes = {
            @Index(name = "idx_notification_user", columnList = "user_id"),
            @Index(name = "idx_notification_type", columnList = "notification_type_id"),
            @Index(name = "idx_notification_sent_at", columnList = "sent_at")
        }
)
public class Notification implements Serializable, MyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notificationId;

    @Column(name = "notification_title", nullable = false, length = 100)
    private String notificationTitle;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;

    @Column(name = "is_read", nullable = false)
    private boolean read = false;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_type_id", nullable = false)
    private NotificationType type;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Notification() {
    }

    public Notification(Long notificationId, String notificationTitle, String message,
            LocalDateTime sentAt, NotificationType type, User user) {
        this.notificationId = notificationId;
        this.notificationTitle = notificationTitle;
        this.message = message;
        this.sentAt = sentAt;
        this.type = type;
        this.user = user;
    }

    public Notification(String notificationTitle, String message,
            LocalDateTime sentAt, NotificationType type, User user) {
        this.notificationTitle = notificationTitle;
        this.message = message;
        this.sentAt = sentAt;
        this.type = type;
        this.user = user;
    }

    @PrePersist
    private void onCreate() {
        if (sentAt == null) {
            sentAt = LocalDateTime.now();
        }
    }

    public Long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }

    public String getNotificationTitle() {
        return notificationTitle;
    }

    public void setNotificationTitle(String notificationTitle) {
        this.notificationTitle = notificationTitle;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Notification other = (Notification) obj;
        return Objects.equals(this.notificationId, other.notificationId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.notificationId);
    }

//    @Override
//    public String toString() {
//        return "Notification: " + notificationTitle
//                + " (User: " + (user != null ? user.getUserEmail() : "unknown") + ")";
//    }
}

/*package rs.ac.bg.fon.e_learning_platforma_njt.entity.impl;

import rs.ac.bg.fon.e_learning_platforma_njt.entity.lookup.NotificationType;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.MyEntity;

@Entity
@Table(name = "notification")
public class Notification implements Serializable, MyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notificationId;

    @Column(name = "notification_title", nullable = false, length = 100)
    private String notificationTitle;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;

    @ManyToOne(optional = false)
    @JoinColumn(name = "notification_type_id", nullable = false)
    private NotificationType type;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Notification() {
    }

    public Notification(Long notificationId, String notificationTitle, String message, LocalDateTime sentAt, NotificationType type, User user) {
        this.notificationId = notificationId;
        this.notificationTitle = notificationTitle;
        this.message = message;
        this.sentAt = sentAt;
        this.type = type;
        this.user = user;
    }

    public Notification(String notificationTitle, String message, LocalDateTime sentAt, NotificationType type, User user) {
        this.notificationTitle = notificationTitle;
        this.message = message;
        this.sentAt = sentAt;
        this.type = type;
        this.user = user;
    }

    @PrePersist
    private void onCreate() {
        if (sentAt == null) {
            sentAt = LocalDateTime.now();
        }
    }

    public Long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }

    public String getNotificationTitle() {
        return notificationTitle;
    }

    public void setNotificationTitle(String notificationTitle) {
        this.notificationTitle = notificationTitle;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Notification other = (Notification) obj;
        if (!Objects.equals(this.notificationTitle, other.notificationTitle)) {
            return false;
        }
        return Objects.equals(this.notificationId, other.notificationId);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Notification: ").append(notificationTitle)
                .append(" (User: ").append(user != null ? user.getUserEmail() : "unknown").append(")");
        return sb.toString();
    }

}
 */
