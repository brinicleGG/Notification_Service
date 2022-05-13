package com.example.notification_service.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends CrudRepository<Notification, Integer> {

    List<Notification> findAllByMessage(String notificationText);

    List<Notification> findAllByTimeBefore(LocalDate sysdate);

    List<Notification> findAllByExternalId(String externalId);

    List<Notification> findAllByStatus(NotificationStatus status);

    List<Notification> findAllByTime(LocalDateTime time);
}
