package ru.practicum.controller.privateControllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.*;
import ru.practicum.dto.participationRequest.EventRequestStatusUpdateRequest;
import ru.practicum.dto.participationRequest.EventRequestStatusUpdateResult;
import ru.practicum.dto.participationRequest.ParticipationRequestDto;
import ru.practicum.service.EventService;
import ru.practicum.service.ParticipationRequestsService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/events")
@RequiredArgsConstructor
@Slf4j
public class EventsPrivateController {

    private final EventService eventService;
    private final ParticipationRequestsService requestsService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable(name = "userId") long userId,
                                    @Valid @RequestBody NewEventDto newEventDto) {

        EventFullDto eventDto = eventService.create(userId, newEventDto);
        log.info("event created - {}", eventDto);

        return eventDto;
    }


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getUserEvents(@PathVariable(name = "userId") @Positive long userId,
                                             @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
                                             @RequestParam(name = "size", defaultValue = "10") @PositiveOrZero int size) {

        List<EventShortDto> list = eventService.getAllUserEvents(userId, from, size);
        log.info("Events found, userId - {}, from - {}, size - {}", userId, from, size);

        return list;
    }


    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getUserEventById(@PathVariable(name = "userId") @Positive long userId,
                                         @PathVariable(name = "eventId") @Positive long eventId) {

        EventFullDto eventFullDto = eventService.getUserEventById(userId, eventId);
        log.info("Event found, id - {}", eventId);

        return eventFullDto;
    }


    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEvent(@PathVariable(name = "userId") @Positive long userId,
                                    @PathVariable(name = "eventId") @Positive long eventId,
                                    @Valid @RequestBody UpdateEventDto updatedEvent) {

        EventFullDto eventFullDto = eventService.updateByUser(userId, eventId, updatedEvent);
        log.info("Event updated, id - {}", eventId);

        return eventFullDto;
    }


    @GetMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getUserParticipationRequests(@PathVariable(name = "userId") @Positive long userId,
                                                                      @PathVariable(name = "eventId") @Positive long eventId) {

        List<ParticipationRequestDto> requests = requestsService.getUserParticipationRequests(userId, eventId);
        log.info("Participation requests founded, eventId - {}, userId - {}", eventId, userId);

        return requests;
    }


    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResult updateParticipationRequestsStatus(@PathVariable(name = "userId") @Positive long userId,
                                                                            @PathVariable(name = "eventId") @Positive long eventId,
                                                                            @RequestBody EventRequestStatusUpdateRequest updateRequest) {

        EventRequestStatusUpdateResult updateResult = requestsService
                .updateParticipationRequestsStatus(userId, eventId, updateRequest);

        log.info("Participation requests updated, eventId - {}, userId - {}, status - {}",
                eventId, userId, updateRequest.getStatus().toString());

        return updateResult;
    }

}
