package rs.ac.bg.fon.e_learning_platforma_njt.entity.lookup;

import jakarta.persistence.*;
import java.util.Objects;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.MyEntity;

@Entity
@Table(name = "notification_type")
public class NotificationType implements MyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_type_id")
    private Long notificationTypeId;

    @Column(name = "notification_type_name", nullable = false, unique = true, length = 50)
    private String notificationTypeName;

    public NotificationType() {
    }

    public NotificationType(Long notificationTypeId) {
        this.notificationTypeId = notificationTypeId;
    }

    public NotificationType(String notificationTypeName) {
        this.notificationTypeName = notificationTypeName;
    }

    public Long getNotificationTypeId() {
        return notificationTypeId;
    }

    public void setNotificationTypeId(Long notificationTypeId) {
        this.notificationTypeId = notificationTypeId;
    }

    public String getNotificationTypeName() {
        return notificationTypeName;
    }

    public void setNotificationTypeName(String notificationTypeName) {
        this.notificationTypeName = notificationTypeName;
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
        final NotificationType other = (NotificationType) obj;
        if (!Objects.equals(this.notificationTypeName, other.notificationTypeName)) {
            return false;
        }
        return Objects.equals(this.notificationTypeId, other.notificationTypeId);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("NotificationType: ").append(notificationTypeName);
        return sb.toString();
    }

}
