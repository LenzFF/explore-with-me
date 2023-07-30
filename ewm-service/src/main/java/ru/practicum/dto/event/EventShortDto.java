package ru.practicum.dto.event;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.user.UserShortDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class EventShortDto {

    private long id;

    @NotBlank
    private String annotation;

    @NotNull
    private CategoryDto category;

    private long confirmedRequests;

    @NotBlank
    private String eventDate;

    @NotNull
    private UserShortDto initiator;

    @NotNull
    private boolean paid;

    @NotBlank
    private String title;

    private long views;

    private long rating;
}
