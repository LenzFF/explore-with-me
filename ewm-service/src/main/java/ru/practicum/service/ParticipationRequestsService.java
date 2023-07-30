package ru.practicum.service;

import ru.practicum.dto.participationRequest.EventRequestStatusUpdateRequest;
import ru.practicum.dto.participationRequest.EventRequestStatusUpdateResult;
import ru.practicum.dto.participationRequest.ParticipationRequestDto;

import java.util.List;

public interface ParticipationRequestsService {

    ParticipationRequestDto create(long userId, long eventId);

    List<ParticipationRequestDto> getUserRequests(long userId);

    ParticipationRequestDto cancelParticipationRequest(long userId, long requestId);

    EventRequestStatusUpdateResult updateParticipationRequestsStatus(long userId, long eventId, EventRequestStatusUpdateRequest updateRequest);

    List<ParticipationRequestDto> getUserParticipationRequests(long userId, long eventId);

    long getCountConfirmedRequestsByEvent(long eventId);

    ParticipationRequestDto getUserRequestOnEvent(long userId, long eventId);

}
