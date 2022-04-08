package com.example.notification_service.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface NotificationRepository extends CrudRepository<Notification, Integer> {

    List<Notification> findAllByNotificationText(String notificationText);

    List<Notification> findByDateBefore(LocalDate sysdate);
}
