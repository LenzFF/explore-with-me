package ru.practicum.dto.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;
import ru.practicum.dto.location.LocationDto;

import javax.validation.constraints.Size;

@Getter
@Setter
public class UpdateEventAdminRequest {

    @Nullable
    @Size(min = 20, max = 2000)
    private String annotation;

    private long category;

    @Nullable
    @Size(min = 20, max = 7000)
    private String description;

    private String eventDate;

    private LocationDto location;

    private Boolean paid;

    private long participantLimit;

    private Boolean requestModeration;

    private String stateAction;

    @Nullable
    @Size(min = 3, max = 120)
    private String title;
}