package ru.practicum.controller.adminControllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.UpdateEventDto;
import ru.practicum.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/events")
@RequiredArgsConstructor
@Slf4j
public class EventAdminController {

    private final EventService eventService;


    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateByAdmin(@PathVariable(name = "eventId") @Positive long eventId,
                                      @Valid @RequestBody UpdateEventDto request) {

        EventFullDto eventFullDto = eventService.updateByAdmin(eventId, request);
        log.info("event updated by admin, eventId - {}, ", eventId);

        return eventFullDto;
    }


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDto> searchEventsByAdmin(@RequestParam(name = "users", required = false) List<Long> users,
                                                  @RequestParam(name = "states", required = false) List<String> states,
                                                  @RequestParam(name = "categories", required = false) List<Long> categories,
                                                  @RequestParam(name = "rangeStart", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                                  @RequestParam(name = "rangeEnd", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                                  @RequestParam(name = "sort", required = false) String sort,
                                                  @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
                                                  @RequestParam(name = "size", defaultValue = "10") @PositiveOrZero int size) {

        List<EventFullDto> events = eventService.searchByAdmin(users, states, categories, rangeStart, rangeEnd, sort, from, size);
        log.info("event search performed by the administrator");

        return events;
    }
}
