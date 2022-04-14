package com.example.notification_service;

import com.example.notification_service.model.NotificationRepository;
import com.example.notification_service.scheduler.ScheduledTasks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.notification_service.model.Notification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@RestController
public class NSController { //TODO: Рефакторинг

    private static final Logger LOGGER = Logger.getLogger(NSController.class.getName());

    @Autowired
    private NotificationRepository notificationRepository;

    @GetMapping("/")
    public List<Notification> list() {

        Iterable<Notification> notificationIterable = notificationRepository.findAll();
        ArrayList<Notification> notifications = new ArrayList<>();
        for (Notification notification : notificationIterable) {
            notifications.add(notification);
        }
        return notifications;
    }

    @PostMapping ("/")
    public ResponseEntity postNotifications(@RequestBody Notification notification) {
        notificationRepository.save(notification);
        return ResponseEntity.status(HttpStatus.OK).body(notification);
    }

    @GetMapping("/{id}")
    public ResponseEntity getNotificationsId(@PathVariable int id) {
        Optional<Notification> optionalNotification = notificationRepository.findById(id);
        if(!optionalNotification.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return new ResponseEntity(optionalNotification.get(),HttpStatus.OK);
    }

    @PatchMapping ("/{id}") //TODO: Рефакторинг, продумать реализацию метода
    public ResponseEntity patchNotifications(@PathVariable int id, @RequestBody Notification patchNotification) {
        Optional<Notification> optionalNotification = notificationRepository.findById(id);
        if(!optionalNotification.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        Notification notification = optionalNotification.get();
        notificationRepository.save(notification);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteNotifications(@PathVariable int id) {
        notificationRepository.deleteById(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/past")
    public List<Notification> getNotificationsPast() {
        LocalDate sysdate = LocalDate.now();
        Iterable<Notification> notificationIterable = notificationRepository.findByTimeBefore(sysdate);
        ArrayList<Notification> notifications = new ArrayList<>();
        for (Notification notification : notificationIterable) {
            notifications.add(notification);
        }
        return notifications;
    }

    @GetMapping("/status/{status}") //TODO: Размножить на все статусы, findAllByMessage(status) на статусы
    public List<Notification> getNotificationsStatus(@PathVariable String status) {
        Iterable<Notification> notificationIterable = notificationRepository.findAllByMessage(status);
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
