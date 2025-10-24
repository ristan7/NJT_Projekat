package rs.ac.bg.fon.e_learning_platforma_njt.dto.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import rs.ac.bg.fon.e_learning_platforma_njt.dto.Dto;
import rs.ac.bg.fon.e_learning_platforma_njt.validation.OneOfLong;

/**
 * DTO za Notification.
 */
public class NotificationDto implements Dto {

    @Positive(message = "Notification ID must be positive.")
    private Long notificationId;

    @NotBlank(message = "Title is required.")
    @Size(max = 100, message = "Title can be at most 100 characters.")
    private String notificationTitle;

    @NotBlank(message = "Message is required.")
    private String message;

    @NotNull(message = "Notification type ID is required.")
    @Positive(message = "Notification type ID must be positive.")
    @OneOfLong(
            value = {1, 2, 3, 4, 5},
            message = "Notification type must be one of: 1 (SYSTEM), 2 (ENROLLMENT), 3 (COURSE), 4 (CERTIFICATE), 5 (REVIEW)."
    )
    private Long notificationTypeId;

    @NotNull(message = "User ID is required.")
    @Positive(message = "User ID must be positive.")
    private Long userId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private boolean read;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime sentAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String notificationTypeName;

    public NotificationDto() {
    }

    /* Getteri/Setteri */
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

    public Long getNotificationTypeId() {
        return notificationTypeId;
    }

    public void setNotificationTypeId(Long notificationTypeId) {
        this.notificationTypeId = notificationTypeId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public String getNotificationTypeName() {
        return notificationTypeName;
    }

    public void setNotificationTypeName(String notificationTypeName) {
        this.notificationTypeName = notificationTypeName;
    }
}
