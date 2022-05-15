package com.example.notification_service;

import com.example.notification_service.model.NotificationRepository;
import com.example.notification_service.model.NotificationStatus;
import com.example.notification_service.model.NotificationType;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.notification_service.model.Notification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
public class NSController {

    private static final Logger LOGGER = Logger.getLogger(NSController.class.getName());

    @Autowired
    private NotificationRepository notificationRepository;

    @PostMapping ("/")
    public ResponseEntity postNotification(@RequestBody Notification notification) {

        LocalDateTime sysdate = LocalDateTime.now();
        if (sysdate.isAfter(notification.getTime())) { // Дата уже прошла
            LOGGER.log(Level.INFO, "Notification not created. Can't create a reminder in the past!");
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
                    .body("Notification not created. Can't create a reminder in the past!");
        }

        String extParams = notification.getExtraParams();
        if (notification.getNotificationType().equals(NotificationType.MAIL)) {
            if (emailIsNotValid(extParams)) {
                return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body("Email is invalid!");
            }
        }

        if (notification.getNotificationType().equals(NotificationType.HTTP)) {
            if (URLIsNotValid(extParams)) {
                return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body("URL is invalid!");
            }
        }

        Iterable<Notification> notificationIterable = notificationRepository.findAllByTime(notification.getTime());

        for (Notification ntf : notificationIterable) {
            if (ntf.getMessage().equals(notification.getMessage()) &&  // такое уведмление уже есть
                    ntf.getExtraParams().equals(notification.getExtraParams())) {
                LOGGER.log(Level.INFO, "Notification not created. Such notification already exists. id = '{0}'."
                        , ntf.getId());
                return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                        .body("Notification not created. Such notification already exists. id = " + ntf.getId());
            }
        }

        notificationRepository.save(notification);
        LOGGER.log(Level.INFO, "Added new notification, id = {0}.", notification.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(notification);
    }

    @PatchMapping ("/{id}") //TODO: Рефакторинг, продумать реализацию метода
    public ResponseEntity patchNotification(@PathVariable String id, @RequestBody Notification patchNotification) {

        if (idIsNotValid(id)) {
            LOGGER.log(Level.INFO, "Bad request. ID is not number, id = {0}.", id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("'" + id + "' is not number!");
        }

        int idd = Integer.parseInt(id);
        boolean flag = false;
        LocalDateTime sysdate = LocalDateTime.now();

        if (sysdate.isAfter(patchNotification.getTime())) { // Дата уже прошла
            LOGGER.log(Level.INFO, "Notification not update. Can't create a reminder in the past!");
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
                    .body("Notification not created. Can't update a reminder in the past!");
        }

        String extParams = patchNotification.getExtraParams();
        if (patchNotification.getNotificationType().equals(NotificationType.MAIL)) {
            if (emailIsNotValid(extParams)) {
                return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body("Email is invalid!");
            }
        }

        if (patchNotification.getNotificationType().equals(NotificationType.HTTP)) {
            if (URLIsNotValid(extParams)) {
                return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body("URL is invalid!");
            }
        }

        Optional<Notification> optionalNotification = notificationRepository.findById(idd);
        if(!optionalNotification.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        Notification notification = optionalNotification.get();

        if (!patchNotification.getMessage().equals(notification.getMessage())) {
            notification.setMessage(patchNotification.getMessage());
            flag = true;
        }

        if (!patchNotification.getNotificationType().equals(notification.getNotificationType())) {
            notification.setNotificationType(patchNotification.getNotificationType());
            flag = true;
        }

        if (!patchNotification.getExtraParams().equals(notification.getExtraParams())) {
            notification.setExtraParams(patchNotification.getExtraParams());
            flag = true;
        }

        if (!patchNotification.getTime().equals(notification.getTime())) {
            notification.setTime(patchNotification.getTime());
            flag = true;
        }

        if (!patchNotification.getExternalId().equals(notification.getExternalId())) {
            notification.setExternalId(patchNotification.getExternalId());
            flag = true;
        }

        if (flag) {
            notificationRepository.save(notification);
        }

        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/{id}") //TODO: менять статус или удалять навсегда?
    public ResponseEntity deleteNotification(@PathVariable String id) {

        if (idIsNotValid(id)) {
            LOGGER.log(Level.INFO, "Bad request. ID is not number, id = {0}.", id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("'" + id + "' is not number!");
        }

        int idd = Integer.parseInt(id);

        Optional<Notification> optionalNotification = notificationRepository.findById(idd);
        if(!optionalNotification.isPresent()) {
            LOGGER.log(Level.INFO, "Not found: Notification with id = {0} not found.", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Notification with id '" + id + "' not found");
        }

        notificationRepository.deleteById(idd);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/")
    public ResponseEntity getListNotifications() {

        Iterable<Notification> notificationIterable = notificationRepository.findAll();
        ArrayList<Notification> notifications = new ArrayList<>();
        for (Notification notification : notificationIterable) {
            notifications.add(notification);
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                notifications.size() != 0 ? notifications : "Notifications list clear!");
    }

    @GetMapping ("/{id}")
    public ResponseEntity getNotificationId(@PathVariable String id) {

        if (idIsNotValid(id)) {
            LOGGER.log(Level.INFO, "Bad request. ID is not number, id = {0}.", id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("'" + id + "' is not number!");
        }

        int idd = Integer.parseInt(id);

        Optional<Notification> optionalNotification = notificationRepository.findById(idd);
        if(!optionalNotification.isPresent()) {
            LOGGER.log(Level.INFO, "Not found: Notification with id = {0} not found.", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Notification with id '" + id + "' not found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(optionalNotification.get());
    }

    @GetMapping("/external_id/{externalId}")
    public ResponseEntity getNotificationExternalId(@PathVariable String externalId) {

        Iterable<Notification> notificationIterable = notificationRepository.findAllByExternalId(externalId);
        ArrayList<Notification> notifications = new ArrayList<>();
        for (Notification notification : notificationIterable) {
            notifications.add(notification);
        }
        if (notifications.size() == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("External ID '" + externalId + "' not found!");
        }
        return ResponseEntity.status(HttpStatus.OK).body(notifications);
    }

    @GetMapping("/past")
    public ResponseEntity getNotificationsPast() {

        LocalDateTime sysdate = LocalDateTime.now();
        Iterable<Notification> notificationIterable = notificationRepository.findAllByTimeBefore(sysdate);
        ArrayList<Notification> notifications = new ArrayList<>();
        for (Notification notification : notificationIterable) {
            notifications.add(notification);
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                notifications.size() != 0 ? notifications : "Notifications list clear!");
    }

    @GetMapping("/status/{status}")
    public ResponseEntity getNotificationsStatus(@PathVariable NotificationStatus status) {

        Iterable<Notification> notificationIterable = notificationRepository.findAllByStatus(status);
        ArrayList<Notification> notifications = new ArrayList<>();
        for (Notification notification : notificationIterable) {
            notifications.add(notification);
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                notifications.size() != 0 ? notifications : "Notifications list clear!");
    }

    @GetMapping("/date/{date}") //TODO: Валидация даты
    public ResponseEntity getNotificationsDate(@PathVariable LocalDateTime stringDate) {

        return ResponseEntity.status(HttpStatus.GONE).body("такой ответ сервер посылает, " +
                "если ресурс раньше был по указанному URL, " +
                "но был удалён и теперь недоступен. " +
                "Серверу в этом случае неизвестно и местоположение " +
                "альтернативного документа (например копии). " +
                "Появился в HTTP/1.1.");
    }

    private static boolean idIsNotValid(String id) {

        try {
            int idd = Integer.parseInt(id);
            return false;
        } catch (NumberFormatException ex) {
            return true;
        }
    }

    private static boolean emailIsNotValid(String email) {

        return email.contains("@") ? false :  true;
    }

    private static boolean URLIsNotValid(String URL) {

        UrlValidator urlValidator = new UrlValidator();
        return !urlValidator.isValid(URL);
    }
}
