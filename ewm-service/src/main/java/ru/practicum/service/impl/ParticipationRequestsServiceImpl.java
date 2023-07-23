package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventMapper;
import ru.practicum.dto.participationRequest.EventRequestStatusUpdateRequest;
import ru.practicum.dto.participationRequest.EventRequestStatusUpdateResult;
import ru.practicum.dto.participationRequest.ParticipationMapper;
import ru.practicum.dto.participationRequest.ParticipationRequestDto;
import ru.practicum.dto.user.UserMapper;
import ru.practicum.exception.CreateConditionException;
import ru.practicum.exception.DataAlreadyExistException;
import ru.practicum.exception.DataNotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.model.*;
import ru.practicum.repository.ParticipationRequestsRepository;
import ru.practicum.service.EventService;
import ru.practicum.service.ParticipationRequestsService;
import ru.practicum.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParticipationRequestsServiceImpl implements ParticipationRequestsService {

    private final ParticipationRequestsRepository requestsRepository;
    private final EventService eventService;
    private final UserService userService;


    @Override
    @Transactional
    public ParticipationRequestDto create(long userId, long eventId) {

        EventFullDto event = eventService.getById(eventId);
        User requester = UserMapper.fromUserDto(userService.get(userId));

        ParticipationRequest duplicatedRequest = requestsRepository.findAllByEventIdAndRequesterId(eventId, userId);

        if (duplicatedRequest != null) {
            throw new DataAlreadyExistException("Participation request already exist, id - " + duplicatedRequest.getId());
        }

        if (event.getInitiator().getId() == userId) {
            throw new CreateConditionException("the user cannot create a request to participate in his event");
        }

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new CreateConditionException("event is not published, eventId - " + eventId);
        }

        ParticipationRequest newPartRequest = new ParticipationRequest();
        newPartRequest.setRequester(requester);
        newPartRequest.setEvent(EventMapper.fromEventFullDto(event, requester));
        newPartRequest.setCreated(LocalDateTime.now());

        if (event.getParticipantLimit() != 0) {
            if (isMoreAvailablePlacesToParticipate(event) == false) {
                throw new CreateConditionException("the event has reached the limit of participants, eventId - " + eventId);
            }
        }

        if (event.getParticipantLimit() == 0 || !event.isRequestModeration())
            newPartRequest.setStatus(RequestStatus.CONFIRMED);

        ParticipationRequestDto requestDto = ParticipationMapper.toParticipationRequestDto(requestsRepository
                .save(newPartRequest));

        updateEventConfirmedRequests(eventId);
        return requestDto;
    }


    @Transactional
    private void updateEventConfirmedRequests(long eventId) {
        EventFullDto event = eventService.getById(eventId);
        User initiator = UserMapper.fromUserDto(userService.get(event.getInitiator().getId()));
        event.setConfirmedRequests(getCountConfirmedRequestsByEvent(eventId));
        eventService.update(EventMapper.fromEventFullDto(event, initiator));
    }


    @Override
    public List<ParticipationRequestDto> getUserRequests(long userId) {

        userService.get(userId);

        List<ParticipationRequest> requestList = requestsRepository.findAllByRequesterId(userId);

        return requestList.stream()
                .map(ParticipationMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public ParticipationRequestDto cancelParticipationRequest(long userId, long requestId) {

        userService.get(userId);

        ParticipationRequest request = requestsRepository.findById(requestId)
                .orElseThrow(() -> new DataNotFoundException("participation request not exist, requestId - " + requestId));

        request.setStatus(RequestStatus.CANCELED);

        return ParticipationMapper.toParticipationRequestDto(requestsRepository
                .save(request));
    }


    @Override
    public List<ParticipationRequestDto> getUserParticipationRequests(long userId, long eventId) {
        Event event = eventService.findByIdAndInitiatorId(eventId, userId);
        if (event == null) {
            throw new DataNotFoundException("event not exist, id - " + eventId + " , userId - " + userId);
        }

        List<ParticipationRequest> requestList = requestsRepository.findAllByEventId(eventId);

        return requestList.stream()
                .map(ParticipationMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }


    @Override
    public long getCountConfirmedRequestsByEvent(long eventId) {
        return requestsRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
    }


    private boolean isMoreAvailablePlacesToParticipate(EventFullDto event) {
        long confirmedRequests = requestsRepository.countByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED);
        return confirmedRequests < event.getParticipantLimit();
    }


    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateParticipationRequestsStatus(long userId, long eventId, EventRequestStatusUpdateRequest updateRequest) {

        userService.get(userId);
        EventFullDto event = eventService.getById(eventId);

        List<ParticipationRequest> requestList = requestsRepository.findByIdIn(updateRequest.getRequestIds());
        List<ParticipationRequest> pendingRequests = new ArrayList<>();

        for (ParticipationRequest request : requestList) {

            if (request.getEvent().getId() == eventId) {
                if (request.getStatus().equals(RequestStatus.PENDING)) {
                    pendingRequests.add(request);
                } else {
                    throw new ValidationException("request status is not pending, id - " + request.getId());
                }
            } else {
                throw new ValidationException("requestId does not match the eventId, requestId - " + request.getId());
            }
        }

        List<ParticipationRequest> confirmedRequests = new ArrayList<>();
        List<ParticipationRequest> rejectedRequests = new ArrayList<>();

        for (ParticipationRequest request : pendingRequests) {

            updateEventConfirmedRequests(eventId);

            switch (updateRequest.getStatus()) {

                case REJECTED:
                    request.setStatus(RequestStatus.REJECTED);
                    rejectedRequests.add(requestsRepository.save(request));
                    break;

                case CONFIRMED:
                    if (isMoreAvailablePlacesToParticipate(event)) {
                        request.setStatus(RequestStatus.CONFIRMED);
                        confirmedRequests.add(requestsRepository.save(request));
                    } else {

                        request.setStatus(RequestStatus.REJECTED);
                        rejectedRequests.add(requestsRepository.save(request));
                        throw new ValidationException("participation limit for event is reached, eventId - " + eventId);
                    }
                    break;
            }
        }

        updateEventConfirmedRequests(eventId);

        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        result.setConfirmedRequests(confirmedRequests.stream()
                .map(ParticipationMapper::toParticipationRequestDto)
                .collect(Collectors.toList()));

        result.setRejectedRequests(rejectedRequests.stream()
                .map(ParticipationMapper::toParticipationRequestDto)
                .collect(Collectors.toList()));
        return result;
    }
}
