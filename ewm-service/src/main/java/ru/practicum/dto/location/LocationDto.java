package ru.practicum.dto.location;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class LocationDto {

    @NotNull
    private float lat;

    @NotNull
    private float lon;
}
