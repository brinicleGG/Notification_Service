package com.example.notification_service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import model.Notification;

import java.util.Date;
import java.util.List;

@RestController
public class NSController {

    @GetMapping("/")
    public List<Notification> list() {
        return null;
    }

    @GetMapping("/{date}")
    public String getNotificationsDate() {
        return (new Date()).toString();
    }

    @GetMapping("/{id}")
    public ResponseEntity getNotificationsId(@PathVariable int id) { //TODO: сделать по методу в каждый контроллер
        /*Notification notification = Dd.getNotification(id);
        if (notification = null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }*/
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/past")
    public String getNotificationsPast() {
        return (new Date()).toString();
    }

    @GetMapping("/{status}")
    public String getNotificationsStatus() {
        return (new Date()).toString();
    }

    @PostMapping ("/")
    public String postNotifications() {
        return (new Date()).toString();
    }

    @PatchMapping ("/{id}")
    public String patchNotifications() {
        return (new Date()).toString();
    }

    @DeleteMapping("/{id}")
    public String deleteNotifications() {
        return (new Date()).toString();
    }
}
