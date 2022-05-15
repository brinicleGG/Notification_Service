package com.example.notification_service.validator;

import com.example.notification_service.NSController;
import com.example.notification_service.model.Notification;
import com.example.notification_service.model.NotificationType;

import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public interface NotificationValidator {

    static final Logger LOGGER = Logger.getLogger(NotificationValidator.class.getName());

    static boolean validation(Notification notification) {
        if (notification.getNotificationType().equals(NotificationType.HTTP)) {
            try {
                (new java.net.URL(notification.getExtraParams())).openStream().close();
                return true;
            } catch (UnknownHostException uhEx) {
                LOGGER.log(Level.INFO, uhEx.toString());
                return true;
            } catch (MalformedURLException mURLEx) {
                LOGGER.log(Level.INFO, mURLEx.toString());
            } catch (Exception e) {
                LOGGER.log(Level.INFO, e.toString());
            }
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
