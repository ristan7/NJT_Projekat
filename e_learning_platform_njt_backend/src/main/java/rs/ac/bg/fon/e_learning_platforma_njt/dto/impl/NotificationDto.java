package rs.ac.bg.fon.e_learning_platforma_njt.dto.impl;

import rs.ac.bg.fon.e_learning_platforma_njt.dto.Dto;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import rs.ac.bg.fon.e_learning_platforma_njt.validation.OneOfLong;

/**
 * DTO za entitet Notification.
 */
public class NotificationDto implements Dto {

    @Positive(message = "Notification id must be a positive number.")
    private Long notificationId;

    @NotBlank(message = "Title is required.")
    @Size(max = 100, message = "Title can be at most 100 characters.")
    private String notificationTitle;

    @NotBlank(message = "Message is required.")
    @Size(max = 2000, message = "Message can be at most 2000 characters.")
    private String message;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime sentAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private boolean read;

    @NotNull(message = "Notification type id is required.")
    @Positive(message = "Notification type id must be positive.")
    @OneOfLong(
            value = {1, 2, 3, 4, 5, 6},
            message = "Notification type id must be one of predefined values: "
            + "1 (SYSTEM), 2 (ENROLLMENT), 3 (PAYMENT), "
            + "4 (COURSE), 5 (CERTIFICATE), 6 (REVIEW)."
    )
    private Long notificationTypeId;

    @NotNull(message = "User id is required.")
    @Positive(message = "User id must be positive.")
    private Long userId;

    public NotificationDto() {
    }

    // ✅ Konstruktor sa 'read' – ovaj koristi mapper
    public NotificationDto(Long notificationId, String notificationTitle, String message,
            LocalDateTime sentAt, boolean read,
            Long notificationTypeId, Long userId) {
        this.notificationId = notificationId;
        this.notificationTitle = notificationTitle;
        this.message = message;
        this.sentAt = sentAt;
        this.read = read;
        this.notificationTypeId = notificationTypeId;
        this.userId = userId;
    }

    // (opciono) Stari konstruktor bez 'read' ako ga koristiš negde drugde
    public NotificationDto(Long notificationId, String notificationTitle, String message,
            LocalDateTime sentAt, Long notificationTypeId, Long userId) {
        this(notificationId, notificationTitle, message, sentAt, false, notificationTypeId, userId);
    }

    // Getteri i setteri
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
}


/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
 /*
package rs.ac.bg.fon.e_learning_platforma_njt.dto.impl;

import rs.ac.bg.fon.e_learning_platforma_njt.dto.Dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import rs.ac.bg.fon.e_learning_platforma_njt.validation.OneOfLong;

/**
 *
 * @author mikir
 */
 /*
public class NotificationDto implements Dto {

    @Positive(message = "User id must be a positive number")
    private Long notificationId;

    @NotBlank(message = "Title is required.")
    @Size(max = 100, message = "Title can be at most 100 characters.")
    private String notificationTitle;

    @NotBlank(message = "Message is required.")
    @Size(max = 2000, message = "Message can be at most 2000 characters.")
    private String message;

    // ① Zabrani upis iz requesta (READ_ONLY = ne može da se pošalje)
    @com.fasterxml.jackson.annotation.JsonProperty(access = com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY)
//    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime sentAt;

    @NotNull(message = "Notification type id is required.")
    @Positive(message = "Notification type id must be positive.")
    @OneOfLong(
            value = {1, 2, 3, 4, 5, 6},
            message = "Notification type id must be one of predefined values: "
            + "1 (SYSTEM), 2 (ENROLLMENT), 3 (PAYMENT), "
            + "4 (COURSE), 5 (CERTIFICATE), 6 (REVIEW)."
    )
    private Long notificationTypeId;

    @NotNull(message = "User id is required.")
    @Positive(message = "User id must be positive.")
    private Long userId;

    public NotificationDto() {
    }

    public NotificationDto(Long notificationId, String notificationTitle, String message, LocalDateTime sentAt, Long notificationTypeId, Long userId) {
        this.notificationId = notificationId;
        this.notificationTitle = notificationTitle;
        this.message = message;
        this.sentAt = sentAt;
        this.notificationTypeId = notificationTypeId;
        this.userId = userId;
    }

    public NotificationDto(String notificationTitle, String message,
            LocalDateTime sentAt, Long notificationTypeId, Long userId) {
        this.notificationTitle = notificationTitle;
        this.message = message;
        this.sentAt = sentAt;
        this.notificationTypeId = notificationTypeId;
        this.userId = userId;
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
    
}
 */
