package ru.practicum.dto;

public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }
}