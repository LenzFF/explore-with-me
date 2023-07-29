package ru.practicum.dto.event;

import ru.practicum.dto.category.CategoryMapper;
import ru.practicum.dto.location.LocationMapper;
import ru.practicum.dto.user.UserMapper;
import ru.practicum.model.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EventMapper {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static Event fromNewEventDto(NewEventDto newEventDto, Category category, Location location, User user) {

        Event event = new Event();

        event.setAnnotation(newEventDto.getAnnotation());
        event.setCategory(category);
        event.setCreatedOn(LocalDateTime.now());
        event.setDescription(newEventDto.getDescription());
        event.setEventDate(LocalDateTime.parse(newEventDto.getEventDate(), TIME_FORMAT));
        event.setInitiator(user);
        event.setLocation(location);
        event.setState(EventState.PENDING);
        event.setPaid(newEventDto.isPaid());
        event.setParticipantLimit(newEventDto.getParticipantLimit());
        event.setPublishedOn(LocalDateTime.now());
        event.setRequestModeration(newEventDto.isRequestModeration());
        event.setTitle(newEventDto.getTitle());
        event.setRating(0L);
        return event;
    }


    public static Event fromEventFullDto(EventFullDto eventFullDto, User user) {

        Event event = new Event();

        event.setId(eventFullDto.getId());
        event.setAnnotation(eventFullDto.getAnnotation());
        event.setCategory(CategoryMapper.fromCategoryDto(eventFullDto.getCategory()));
        event.setConfirmedRequests(eventFullDto.getConfirmedRequests());
        event.setCreatedOn(LocalDateTime.now());
        event.setDescription(eventFullDto.getDescription());
        event.setEventDate(LocalDateTime.parse(eventFullDto.getEventDate(), TIME_FORMAT));
        event.setInitiator(user);
        event.setLocation(LocationMapper.fromLocationDto(eventFullDto.getLocation()));
        event.setPaid(eventFullDto.isPaid());
        event.setParticipantLimit(eventFullDto.getParticipantLimit());
        event.setPublishedOn(LocalDateTime.now());
        event.setRequestModeration(eventFullDto.isRequestModeration());
        event.setState(eventFullDto.getState());
        event.setTitle(eventFullDto.getTitle());
        event.setRating(eventFullDto.getRating());
        return event;
    }


    public static EventShortDto toEventShortDto(Event event, long views) {

        EventShortDto shortDto = new EventShortDto();

        shortDto.setId(event.getId());
        shortDto.setAnnotation(event.getAnnotation());
        shortDto.setCategory(CategoryMapper.toCategoryDto(event.getCategory()));
        shortDto.setConfirmedRequests(event.getConfirmedRequests());
        shortDto.setEventDate(event.getEventDate().format(TIME_FORMAT));
        shortDto.setInitiator(UserMapper.toShortDto(event.getInitiator()));
        shortDto.setPaid(event.isPaid());
        shortDto.setTitle(event.getTitle());
        shortDto.setViews(views);
        shortDto.setRating(event.getRating());
        return shortDto;
    }


    public static EventFullDto toEventFullDto(Event event, long views) {
        EventFullDto eventFullDto = new EventFullDto();

        eventFullDto.setId(event.getId());
        eventFullDto.setAnnotation(event.getAnnotation());
        eventFullDto.setCategory(CategoryMapper.toCategoryDto(event.getCategory()));
        eventFullDto.setConfirmedRequests(event.getConfirmedRequests());
        eventFullDto.setCreatedOn(event.getCreatedOn().format(TIME_FORMAT));
        eventFullDto.setDescription(event.getDescription());
        eventFullDto.setEventDate(event.getEventDate().format(TIME_FORMAT));
        eventFullDto.setInitiator(UserMapper.toShortDto(event.getInitiator()));
        eventFullDto.setLocation(LocationMapper.toLocationDto(event.getLocation()));
        eventFullDto.setPaid((event.isPaid()));
        eventFullDto.setParticipantLimit(event.getParticipantLimit());
        eventFullDto.setPublishedOn(event.getPublishedOn().format(TIME_FORMAT));
        eventFullDto.setRequestModeration(event.isRequestModeration());
        eventFullDto.setState(event.getState());
        eventFullDto.setTitle(event.getTitle());
        eventFullDto.setViews(views);
        eventFullDto.setRating(event.getRating());
        return eventFullDto;
    }


    public static EventShortDto toEventShortDto(EventFullDto event) {
        EventShortDto shortDto = new EventShortDto();

        shortDto.setId(event.getId());
        shortDto.setAnnotation(event.getAnnotation());
        shortDto.setCategory(event.getCategory());
        shortDto.setConfirmedRequests(event.getConfirmedRequests());
        shortDto.setEventDate(event.getEventDate());
        shortDto.setInitiator(event.getInitiator());
        shortDto.setPaid(event.isPaid());
        shortDto.setTitle(event.getTitle());
        shortDto.setViews(event.getViews());
        shortDto.setRating(event.getRating());
        return shortDto;
    }
}
