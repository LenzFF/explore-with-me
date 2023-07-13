package ru.practicum.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@RequiredArgsConstructor
public class EndpointHitDto {

    private long id;

    @NotBlank
    @Size(max = 100)
    private String app;

    @NotBlank
    @Size(max = 100)
    private String uri;

    @NotBlank
    @Size(max = 25)
    private String ip;

    @NotBlank
    private String timestamp;
}
