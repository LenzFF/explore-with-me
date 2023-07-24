package ru.practicum.dto.event;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.location.LocationDto;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.model.EventState;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class EventFullDto {

    private long id;

    @NotBlank
    private String annotation;

    @NotNull
    private CategoryDto category;

    private long confirmedRequests;

    @NotBlank
    private String createdOn;

    @NotBlank
    private String description;

    @NotBlank
    private String eventDate;

    @NotNull
    private UserShortDto initiator;

    @NotNull
    private LocationDto location;

    @NotNull
    private boolean paid;

    private long participantLimit = 0;

    @NotBlank
    private String publishedOn;

    private boolean requestModeration = false;

    private EventState state = EventState.PENDING;

    @NotBlank
    private String title;

    private long views;
}
