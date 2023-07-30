package ru.practicum.dto.like;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.user.UserShortDto;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class LikeDto {

    private long id;

    private UserShortDto user;

    private EventShortDto event;

    @NotNull
    private boolean like;
}
