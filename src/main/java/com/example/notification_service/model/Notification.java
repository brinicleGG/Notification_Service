package com.example.notification_service.model;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "external_id")
    private String externalId;  //TODO: Спросить, что с этим делать

    private String message;

    @Column(nullable = false)
    private LocalDate time;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", columnDefinition = "enum", nullable = false)
    private NotificationType notificationType;

    @Column(name = "extra_params", nullable = false)
    private String extraParams;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum")
    private NotificationStatus status = NotificationStatus.WAITING;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDate getTime() {
        return time;
    }

    public void setTime(LocalDate time) {
        this.time = time;
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    public String getExtraParams() {
        return extraParams;
    }

    public void setExtraParams(String extraParams) {
        this.extraParams = extraParams;
    }

    public NotificationStatus getStatus() {
        return status;
    }

    public void setStatus(NotificationStatus status) {
        this.status = status;
    }
}
