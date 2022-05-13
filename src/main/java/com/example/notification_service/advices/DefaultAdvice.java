package com.example.notification_service.advices;

import com.example.notification_service.exceptions.ExtraParamsException;
import com.example.notification_service.exceptions.NotificationTypeException;
import org.hibernate.exception.JDBCConnectionException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class DefaultAdvice {

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class) // 415 не JSON
    public ResponseEntity typeMotSupportedException(HttpMediaTypeNotSupportedException e) {
        return new ResponseEntity(e.getMessage(), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler(DataIntegrityViolationException.class) // 400 нет обязательных полей
    public ResponseEntity invalidDataException(DataIntegrityViolationException e) {
        return new ResponseEntity("Не указаны обязательные поля.", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class) // 422 поля не валидны
    public ResponseEntity notReadableException(HttpMessageNotReadableException e) {
        return new ResponseEntity("Неправельный формат полей :" + e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(ExtraParamsException.class) // 412 не коректный url или email
    public ResponseEntity notificationTypeException(ExtraParamsException e) {
        return new ResponseEntity(e.getMessage(), HttpStatus.PRECONDITION_FAILED);
    }

    @ExceptionHandler(JDBCConnectionException.class) // 503 нет доступа к БД
    public ResponseEntity notificationTypeException(JDBCConnectionException e) {
        return new ResponseEntity(e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
    }
}
