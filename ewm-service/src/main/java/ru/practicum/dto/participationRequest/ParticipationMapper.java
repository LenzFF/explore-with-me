package ru.practicum.dto.participationRequest;

import ru.practicum.model.ParticipationRequest;
import java.time.format.DateTimeFormatter;

public class ParticipationMapper {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static ParticipationRequestDto toParticipationRequestDto(ParticipationRequest request) {

        ParticipationRequestDto dto = new ParticipationRequestDto();
        dto.setId(request.getId());
        dto.setRequester(request.getRequester().getId());
        dto.setEvent(request.getEvent().getId());
        dto.setCreated(request.getCreated().format(TIME_FORMAT));
        dto.setStatus(request.getStatus().toString());

        return dto;
    }
}
