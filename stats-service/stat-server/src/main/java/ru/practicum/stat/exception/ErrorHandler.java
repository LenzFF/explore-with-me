package ru.practicum.stat.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.dto.ValidationException;

import java.io.PrintWriter;
import java.io.StringWriter;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleException(Exception e) {
        log.error("Error", e);

        StringWriter out = new StringWriter();
        e.printStackTrace(new PrintWriter(out));
        String stacktrace = out.toString();

        return new ApiError(e.getMessage() + stacktrace);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ValidationException.class})
    public ApiError handleValidationException(ValidationException e) {
        log.error("Error", e);

        StringWriter out = new StringWriter();
        e.printStackTrace(new PrintWriter(out));
        String stacktrace = out.toString();

        return new ApiError(e.getMessage() + stacktrace);
    }
}
