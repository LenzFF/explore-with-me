package ru.practicum.service.impl;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatClient;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.CategoryMapper;
import ru.practicum.dto.event.*;
import ru.practicum.dto.location.LocationDto;
import ru.practicum.dto.user.UserMapper;
import ru.practicum.exception.CreateConditionException;
import ru.practicum.exception.DataNotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.exception.WrongParameterException;
import ru.practicum.model.*;
import ru.practicum.repository.EventRepository;
import ru.practicum.service.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.HOURS;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {


    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final EventRepository eventRepository;
    private final UserService userService;
    private final CategoryService categoryService;
    private final LocationService locationService;
    private final StatClient statClient;


    @Override
    public long getEventStats(long eventId) {

        String start = URLDecoder.decode(LocalDateTime.of(2000, 1, 1, 1, 1).format(TIME_FORMAT), StandardCharsets.UTF_8);
        String end = URLDecoder.decode(LocalDateTime.now().format(TIME_FORMAT), StandardCharsets.UTF_8);
        String[] uri = {"/events/" + eventId};
        List<ViewStatsDto> statDto = statClient.getStats(start, end, uri, true);

        if (statDto.size() > 0) {
            return statDto.get(0).getHits();
        }

        return 0;
    }


    @Override
    public Map<Long, Long> getEventsStatsByIds(List<Long> eventsIds) {

        List<String> uris = eventsIds.stream()
                .map(i -> "/events/" + i)
                .collect(Collectors.toList());

        String[] urisArray = new String[uris.size()];
        uris.toArray(urisArray);

        String start = URLDecoder.decode(LocalDateTime.of(2000, 1, 1, 1, 1).format(TIME_FORMAT), StandardCharsets.UTF_8);
        String end = URLDecoder.decode(LocalDateTime.now().format(TIME_FORMAT), StandardCharsets.UTF_8);

        List<ViewStatsDto> statsDto = statClient.getStats(start, end, urisArray, true);

        if (statsDto == null || statsDto.isEmpty()) {
            return eventsIds.stream()
                    .collect(Collectors.toMap(e -> e, e -> 0L));
        }

        return statsDto.stream()
                .collect(Collectors.toMap(e -> {
                            String[] splitUri = e.getUri().split("/");
                            return Long.valueOf(splitUri[splitUri.length - 1]);
                        },
                        ViewStatsDto::getHits));
    }


    @Override
    @Transactional
    public EventFullDto create(long userId, NewEventDto newEventDto) {

        LocalDateTime eventTime = LocalDateTime.parse(newEventDto.getEventDate(), TIME_FORMAT);

        validateDateTime(LocalDateTime.now(), eventTime);

        Category category = CategoryMapper.fromCategoryDto(categoryService.getById(newEventDto.getCategory()));
        User owner = UserMapper.fromUserDto(userService.get(userId));

        Location location = locationService.create(newEventDto.getLocation());

        Event newEvent = EventMapper.fromNewEventDto(newEventDto, category, location, owner);
        Event savedEvent = eventRepository.save(newEvent);

        return EventMapper.toEventFullDto(savedEvent, 0);
    }


    @Override
    public List<EventShortDto> getAllUserEvents(long userId, int from, int size) {

        userService.get(userId);
        PageRequest page = PageRequest.of(from / size, size, Sort.by("id").ascending());

        List<Event> events = eventRepository.findAllByInitiatorId(userId, page);

        Map<Long, Long> eventsStats = getEventsStatsByIds(events.stream()
                .map(Event::getId)
                .collect(Collectors.toList()));

        return events.stream()
                .map(event -> EventMapper.toEventShortDto(event, eventsStats.getOrDefault(event.getId(), 0L)))
                .collect(Collectors.toList());
    }


    @Override
    public EventFullDto getById(long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new DataNotFoundException("event not exist, id - " + eventId));

        return EventMapper.toEventFullDto(event,
                getEventStats(eventId));
    }


    @Override
    public EventFullDto getEventByIdAndPostHit(long eventId, HttpServletRequest request) {

        postHit(request);
        EventFullDto eventDto = getById(eventId);

        if (eventDto.getState() != EventState.PUBLISHED) {
            throw new DataNotFoundException("event is not published, id - " + eventId);
        }

        return eventDto;
    }


    private void postHit(HttpServletRequest request) {
        EndpointHitDto endpointHitDto = new EndpointHitDto();
        endpointHitDto.setApp("ewm-main-event-service");
        endpointHitDto.setIp(request.getRemoteAddr());
        endpointHitDto.setTimestamp(LocalDateTime.now().format(TIME_FORMAT));
        endpointHitDto.setUri(request.getRequestURI());

        statClient.postStat(endpointHitDto);
    }


    @Override
    public Set<Event> getEventsByIdIn(Set<Long> eventsIds) {
        return new HashSet<>(eventRepository.findByIdIn(eventsIds));
    }


    @Override
    public Event findByIdAndInitiatorId(long eventId, long userId) {
        return eventRepository.findByIdAndInitiatorId(eventId, userId);
    }


    @Override
    public EventFullDto getUserEventById(long userId, long eventId) {
        userService.get(userId);
        long stat = getEventStats(eventId);

        return EventMapper.toEventFullDto(eventRepository.findById(eventId)
                .orElseThrow(() -> new DataNotFoundException("event not exist, id - " + eventId)), stat);
    }


    @Override
    @Transactional
    public EventFullDto updateByUser(long userId, long eventId, UpdateEventDto request) {

        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId);

        if (event == null) {
            throw new DataNotFoundException("event not exist, id - " + eventId + " ,or cannot be updated by userId - " + userId);
        }

        if (event.getState() == EventState.PUBLISHED) {
            throw new ValidationException("published event cannot be changed, id - " + eventId);
        }

        updateEventByParameters(event, request);

        String stateString = request.getStateAction();
        if (stateString != null && !stateString.isBlank()) {
            switch (StateActionUser.valueOf(stateString)) {

                case CANCEL_REVIEW:
                    event.setState(EventState.CANCELED);
                    break;

                case SEND_TO_REVIEW:
                    event.setState(EventState.PENDING);
                    break;
            }
        }

        Event updatedEvent = eventRepository.save(event);
        long stats = getEventStats(event.getId());

        return EventMapper.toEventFullDto(updatedEvent, stats);
    }


    private void updateEventByParameters(Event event, UpdateEventDto request) {

        String newDateString = request.getEventDate();
        if (newDateString != null && !newDateString.isBlank()) {
            LocalDateTime newDate = LocalDateTime.parse(newDateString, TIME_FORMAT);
            validateDateTime(LocalDateTime.now(), newDate);
            event.setEventDate(newDate);
        }

        String annotation = request.getAnnotation();
        if (annotation != null && !annotation.isBlank()) {
            event.setAnnotation(annotation);
        }

        String description = request.getDescription();
        if (description != null && !description.isBlank()) {
            event.setDescription(description);
        }

        long categoryId = request.getCategory();
        if (categoryId != 0) {
            CategoryDto categoryDto = categoryService.getById(categoryId);
            event.setCategory(CategoryMapper.fromCategoryDto(categoryDto));
        }

        LocationDto location = request.getLocation();
        if (location != null) {
            event.setLocation(locationService
                    .create(request.getLocation()));
        }

        if (request.getPaid() != null) {
            event.setPaid(request.getPaid());
        }

        if (request.getParticipantLimit() != 0) {
            event.setParticipantLimit(request.getParticipantLimit());
        }

        if (request.getRequestModeration() != null) {
            event.setRequestModeration(request.getRequestModeration());
        }

        String title = request.getTitle();
        if (title != null && !title.isBlank()) {
            event.setTitle(title);
        }
    }


    @Override
    @Transactional
    public EventFullDto updateByAdmin(long eventId, UpdateEventDto request) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new DataNotFoundException("event not exist, id - " + eventId));

        updateEventByParameters(event, request);

        String stateString = request.getStateAction();
        if (stateString != null && !stateString.isBlank()) {
            switch (StateActionAdmin.valueOf(stateString)) {

                case PUBLISH_EVENT:
                    if (HOURS.between(LocalDateTime.now(), event.getEventDate()) < 1) {
                        throw new CreateConditionException("There must be at least 1 hours before the event publish");
                    }
                    if (event.getState() == EventState.PUBLISHED) {
                        throw new ValidationException("event is already published, id - " + eventId);
                    }
                    if (event.getState() == EventState.CANCELED) {
                        throw new ValidationException("event is already canceled, id - " + eventId);
                    }
                    event.setState(EventState.PUBLISHED);
                    break;

                case REJECT_EVENT:
                    if (event.getState() == EventState.PUBLISHED) {
                        throw new ValidationException("event is already published, id - " + eventId);
                    }
                    event.setState(EventState.CANCELED);
                    break;
            }
        }

        long stat = getEventStats(event.getId());
        return EventMapper.toEventFullDto(eventRepository.save(event), stat);
    }


    @Override
    @Transactional
    public Event update(Event event) {
        return eventRepository.save(event);
    }


    @Override
    public List<EventShortDto> searchEventsAndPostHits(String text, List<Long> categories, Boolean paid,
                                                       LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                       Boolean onlyAvailable, String sort, int from, int size,
                                                       HttpServletRequest request) {


        postHit(request);
        Pageable pageable = PageRequest.of(from / size, size);

        List<BooleanExpression> conditions = new ArrayList<>();
        List<Event> events;
        QEvent event = QEvent.event;

        addStatesConditions(List.of(EventState.PUBLISHED.toString()), conditions, event);
        addTextCondition(text, conditions, event);
        addCategoriesConditions(categories, conditions, event);
        addPaidCondition(paid, conditions, event);
        addDateConditions(rangeStart, rangeEnd, conditions, event);

        BooleanExpression finalCondition = getFinalCondition(conditions);
        events = eventRepository.findAll(finalCondition, pageable).getContent();

        if (onlyAvailable != null && onlyAvailable) {
            events = eventRepository.findAvailableEvents(events
                    .stream()
                    .map(Event::getId)
                    .collect(Collectors.toSet()), RequestStatus.CONFIRMED.toString());
        }


        Map<Long, Long> eventsStats = getEventsStatsByIds(events.stream()
                .map(Event::getId)
                .collect(Collectors.toList()));

        Comparator<EventShortDto> comparator = getEventComparator(sort);

        return events.stream()
                .map(e -> EventMapper.toEventShortDto(e, eventsStats.getOrDefault(e.getId(), 0L)))
                .sorted(comparator)
                .collect(Collectors.toList());
    }


    private Comparator<EventShortDto> getEventComparator(String sort) {
        if (sort.equals("EVENT_DATE")) {
            return Comparator.comparing(EventShortDto::getEventDate);
        } else {
            return Comparator.comparing(EventShortDto::getViews);
        }
    }


    @Override
    public List<EventFullDto> searchByAdmin(List<Long> users, List<String> states, List<Long> categories,
                                            LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size) {


        Pageable pageable = PageRequest.of(from / size, size);
        List<BooleanExpression> conditions = new ArrayList<>();
        Page<Event> requestPage;
        QEvent event = QEvent.event;

        addUsersConditions(users, conditions, event);
        addStatesConditions(states, conditions, event);
        addCategoriesConditions(categories, conditions, event);
        addDateConditions(rangeStart, rangeEnd, conditions, event);


        if (conditions.size() != 0) {
            BooleanExpression finalCondition = getFinalCondition(conditions);
            requestPage = eventRepository.findAll(finalCondition, pageable);
        } else {
            requestPage = eventRepository.findAll(pageable);
        }


        List<Event> events = requestPage.getContent();
        Map<Long, Long> stat = getEventsStatsByIds(events
                .stream()
                .map(Event::getId)
                .collect(Collectors.toList()));

        return events.stream()
                .map(e -> EventMapper.toEventFullDto(e, stat.getOrDefault(e.getId(), 0L)))
                .collect(Collectors.toList());
    }


    private void addDateConditions(LocalDateTime rangeStart, LocalDateTime rangeEnd, List<BooleanExpression> conditions,
                                   QEvent event) {

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end;

        conditions.add((event.eventDate.eq(start)).or(event.eventDate.gt(start)));

        if (rangeStart != null && rangeEnd != null) {
            start = rangeStart;
            end = rangeEnd;
            validateDateTime(start, end);

            conditions.add((event.eventDate.eq(start)).or(event.eventDate.gt(start)));
            conditions.add((event.eventDate.eq(end)).or(event.eventDate.lt(end)));
        }

        if (rangeStart != null && rangeEnd == null) {
            start = rangeStart;
            conditions.add((event.eventDate.eq(start)).or(event.eventDate.gt(start)));
        }

        if (rangeStart == null && rangeEnd != null) {
            end = rangeEnd;
            conditions.add((event.eventDate.eq(end)).or(event.eventDate.lt(end)));
        }
    }


    private void validateDateTime(LocalDateTime start, LocalDateTime end) {
        if (!start.isBefore(end)) {
            throw new WrongParameterException("There must be at least 2 hours before the event starts");
        }
    }


    private void addCategoriesConditions(List<Long> categories, List<BooleanExpression> conditions, QEvent event) {
        if (categories != null) {
            for (Long categoryId : categories) {
                conditions.add(event.category.id.eq(categoryId));
            }
        }
    }


    private void addUsersConditions(List<Long> users, List<BooleanExpression> conditions, QEvent event) {
        if (users != null) {
            for (Long userId : users) {
                conditions.add(event.initiator.id.eq(userId));
            }
        }
    }


    private void addStatesConditions(List<String> states, List<BooleanExpression> conditions, QEvent event) {
        if (states != null) {
            for (String state : states) {
                EventState eventState = EventState.valueOf(state);
                conditions.add(event.state.eq(eventState));
            }
        }
    }


    private void addTextCondition(String text, List<BooleanExpression> conditions, QEvent event) {
        if (text != null) {
            String anyText = "%";
            String condition = String.format("%s%s%s", anyText, text, anyText);
            conditions.add((event.annotation.likeIgnoreCase(condition)).or(event.description.likeIgnoreCase(condition)));
        }
    }


    private void addPaidCondition(Boolean paid, List<BooleanExpression> conditions, QEvent event) {
        if (paid != null) {
            conditions.add(event.paid.eq(paid));
        }
    }


    private BooleanExpression getFinalCondition(List<BooleanExpression> conditions) {
        return conditions.stream()
                .reduce(BooleanExpression::and)
                .get();
    }
}
