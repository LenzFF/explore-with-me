package ru.practicum.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.event.*;
import ru.practicum.model.Event;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface EventService {

    long getEventStats(long eventId);

    Map<Long, Long> getEventsStatsByIds(List<Long> eventsIds);

    EventFullDto create(long userId, NewEventDto newEventDto);

    @Transactional
    void updateEventAndInitiatorRatings(long eventId, long rating);

    List<EventShortDto> getAllUserEvents(long userId, String sort, int from, int size);

    EventFullDto getById(long eventId);

    Event get(long eventId);

    EventFullDto getUserEventById(long userId, long eventId);

    EventFullDto updateByUser(long userId, long eventId, UpdateEventDto updatedEventUserRequest);

    EventFullDto updateByAdmin(long eventId, UpdateEventDto request);

    List<EventFullDto> searchByAdmin(List<Long> users, List<String> states, List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, String sort, int from, int size);

    EventFullDto getEventByIdAndPostHit(long eventId, HttpServletRequest request);

    Event update(Event event);

    List<EventShortDto> searchEventsAndPostHits(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable, String sort, int from, int size, HttpServletRequest request);

    Set<Event> getEventsByIdIn(Set<Long> eventsIds);

    Event findByIdAndInitiatorId(long eventId, long userId);
}
