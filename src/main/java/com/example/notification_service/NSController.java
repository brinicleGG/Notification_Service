package com.example.notification_service;

import com.example.notification_service.model.NotificationRepository;
import com.example.notification_service.model.NotificationStatus;
import com.example.notification_service.validator.NotificationValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.notification_service.model.Notification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
public class NSController {

    private static final Logger LOGGER = Logger.getLogger(NSController.class.getName());

    @Autowired
    private NotificationRepository notificationRepository;

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



    @PostMapping ("/") //TODO проверку на плохой запрос
    public ResponseEntity postNotification(@RequestBody Notification notification) {

        LocalDateTime ldt = LocalDateTime.now();
        if (ldt.isAfter(notification.getTime())) { // Дата уже прошла
            LOGGER.log(Level.INFO, "Notification not created. Can't create a reminder in the past!");
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
                    .body("Notification not created. Can't create a reminder in the past!");
        }

        if (!NotificationValidator.validation(notification)) {
            LOGGER.log(Level.INFO, "Notification not created. URL or Email is incorrect!");
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
                    .body("Notification not created. URL or Email is incorrect!");
        }

        Iterable<Notification> notificationIterable = notificationRepository.findAllByTime(notification.getTime());

        for (Notification ntf : notificationIterable) {
            if (ntf.getMessage().equals(notification.getMessage()) &&
                    ntf.getExtraParams().equals(notification.getExtraParams())) {
                LOGGER.log(Level.INFO, "Notification not created. Such notification already exists. id = '{0}'.", ntf.getId());
                return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                        .body("Notification not created. Such notification already exists. id = " + ntf.getId());
            }
        }

        notificationRepository.save(notification);
        LOGGER.log(Level.INFO, "Added new notification, id = {0}.", notification.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(notification);
    }



    @GetMapping ("/{id}") // TODO: Integer.MAX_VALUE = 2_147_483_647. Будет искать уведомление с отрицательным ID
    public ResponseEntity getNotificationId(@PathVariable String id) {

        int idd;
        try {
            idd = Integer.parseInt(id);
        } catch (NumberFormatException ex) {

            LOGGER.log(Level.INFO, "Bad request in GET method: id is not number, id = {0}.", id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("'" + id + "' is not number!");
        }

        Optional<Notification> optionalNotification = notificationRepository.findById(idd);
        if(!optionalNotification.isPresent()) {
            LOGGER.log(Level.INFO, "Not found: Notification with id = {0} not found.", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Notification with id '" + id + "' not found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(optionalNotification.get());
    }



    @PatchMapping ("/{id}") //TODO: Рефакторинг, продумать реализацию метода
    public ResponseEntity patchNotification(@PathVariable String id, @RequestBody Notification patchNotification) {

        int idd;
        try {
            idd = Integer.parseInt(id);
        } catch (NumberFormatException ex) {

            LOGGER.log(Level.INFO, "Bad request in PATCH method: id is not number, id = {0}.", id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("'" + id + "' is not number!");
        }

        Optional<Notification> optionalNotification = notificationRepository.findById(idd);
        if(!optionalNotification.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        Notification notification = optionalNotification.get();
        notificationRepository.save(notification);
        return new ResponseEntity(HttpStatus.OK);
    }



    @DeleteMapping("/{id}") //TODO: менять статус или удалять навсегда?
    public ResponseEntity deleteNotification(@PathVariable String id) {

        int idd;
        try {
            idd = Integer.parseInt(id);
        } catch (NumberFormatException ex) {

            LOGGER.log(Level.INFO, "Bad request in DELETE method: id is not number, id = {0}.", id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("'" + id + "' is not number!");
        }

        notificationRepository.deleteById(idd);
        return new ResponseEntity(HttpStatus.OK);
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
    public List<Notification> getNotificationsPast() {

        LocalDate sysdate = LocalDate.now();
        Iterable<Notification> notificationIterable = notificationRepository.findAllByTimeBefore(sysdate);
        ArrayList<Notification> notifications = new ArrayList<>();
        for (Notification notification : notificationIterable) {
            notifications.add(notification);
        }
        return notifications;
    }



    @GetMapping("/status/{status}") //TODO: Размножить на все статусы, findAllByMessage(status) на статусы
    public List<Notification> getNotificationsStatus(@PathVariable NotificationStatus status) {

        Iterable<Notification> notificationIterable = notificationRepository.findAllByStatus(status);
        ArrayList<Notification> notifications = new ArrayList<>();
        for (Notification notification : notificationIterable) {
            notifications.add(notification);
        }
        return notifications;
    }



    @GetMapping("/date/{date}") //TODO: Валидация даты
    public List<Notification> getNotificationsDate(@PathVariable String stringDate) {

        return null;
    }
}
