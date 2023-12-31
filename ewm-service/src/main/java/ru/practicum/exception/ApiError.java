package ru.practicum.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ApiError {

    private List<String> errors;
    private String message;
    private String reason;
    private String status;
    private String timestamp;
}
