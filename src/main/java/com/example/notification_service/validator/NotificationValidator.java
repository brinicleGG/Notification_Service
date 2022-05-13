package com.example.notification_service.validator;

import com.example.notification_service.model.Notification;
import com.example.notification_service.model.NotificationType;

public interface NotificationValidator {

    static boolean validation(Notification notification) {
        if (notification.getNotificationType().equals(NotificationType.HTTP)) {
            try {
                (new java.net.URL(notification.getExtraParams())).openStream().close();
                return true;
            } catch (Exception ex) { }
            return false;
        }

        if (notification.getNotificationType().equals(NotificationType.MAIL)) {
            if (notification.getExtraParams().contains("@")) {
                return true;
            }
            return false;
        }
        return false;
    }
}
