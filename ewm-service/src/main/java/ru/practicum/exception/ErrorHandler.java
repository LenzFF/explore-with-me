package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.postgresql.util.PSQLException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    private static ApiError getApiError(HttpStatus httpStatus, StackTraceElement[] stackTrace, String message) {

        List<String> stackTraceList = Arrays.stream(stackTrace)
                .map(StackTraceElement::toString)
                .collect(Collectors.toList());

        return ApiError.builder()
                .reason(httpStatus.getReasonPhrase())
                .timestamp(LocalDateTime.now().format(TIME_FORMAT))
                .status(httpStatus.name())
                .message(message)
                .build();
    }


    @ExceptionHandler({DataAlreadyExistException.class, CreateConditionException.class, ValidationException.class})
    public ResponseEntity<ApiError> handleDataAlreadyExistException(RuntimeException e) {

        ApiError apiError = getApiError(HttpStatus.CONFLICT, e.getStackTrace(), e.getMessage());
        log.debug(e.getClass().getSimpleName() + ": " + e.getMessage());

        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }


    @ExceptionHandler({WrongParameterException.class, ConstraintViolationException.class})
    public ResponseEntity<ApiError> handleConstraintViolationExceptionException(RuntimeException e) {

        ApiError apiError = getApiError(HttpStatus.BAD_REQUEST, e.getStackTrace(), e.getMessage());

        log.debug(e.getClass().getSimpleName() + ": " + e.getMessage());
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler({DataNotFoundException.class, NoSuchElementException.class})
    public ResponseEntity<ApiError> handleDataNotFoundException(RuntimeException e) {

        ApiError apiError = getApiError(HttpStatus.NOT_FOUND, e.getStackTrace(), e.getMessage());

        log.debug(e.getClass().getSimpleName() + ": " + e.getMessage());
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ApiError> handlePSQLException(final PSQLException e) {

        ApiError apiError = getApiError(HttpStatus.CONFLICT, e.getStackTrace(), e.getMessage());
        log.debug(e.getClass().getSimpleName() + ": " + e.getMessage());

        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }

}
