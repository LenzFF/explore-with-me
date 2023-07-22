package ru.practicum.dto.participationRequest;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParticipationRequestDto {

    private long id;

    private String created;

    private long event;

    private long requester;

    private String status;
}
