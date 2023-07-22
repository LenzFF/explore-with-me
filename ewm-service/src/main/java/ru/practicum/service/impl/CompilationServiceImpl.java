package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.CompilationMapper;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;
import ru.practicum.exception.DataNotFoundException;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;
import ru.practicum.repository.CompilationRepository;
import ru.practicum.service.CompilationService;
import ru.practicum.service.EventService;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventService eventService;


    @Override
    public CompilationDto create(NewCompilationDto newCompilationDto) {

        Set<Long> eventsIds = newCompilationDto.getEvents();
        Set<Event> events = new HashSet<>();

        if (eventsIds != null && !eventsIds.isEmpty()) {
            events = eventService.getEventsByIdIn(eventsIds);
        }

        Compilation compilation = compilationRepository.save(CompilationMapper
                .fromNewCompilationDto(newCompilationDto, events));

        return getCompilationDtoWithEventsStats(compilation);
    }


    @Override
    public void deleteById(long compId) {

        compilationRepository.findById(compId)
                .orElseThrow(() -> new DataNotFoundException("compilation not exist, id - " + compId));

        compilationRepository.deleteById(compId);
    }


    @Override
    public CompilationDto update(long compId, UpdateCompilationRequest updateRequest) {

        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new DataNotFoundException("compilation not exist, id - " + compId));

        if (updateRequest.getTitle() != null) {
            compilation.setTitle(updateRequest.getTitle());
        }

        if (updateRequest.getPinned() != null) {
            compilation.setPinned(updateRequest.getPinned());
        }

        if (updateRequest.getEvents() != null) {
            compilation.getEvents().clear();
            compilation.setEvents(eventService.getEventsByIdIn(updateRequest.getEvents()));

        }

        return getCompilationDtoWithEventsStats(compilation);
    }


    private CompilationDto getCompilationDtoWithEventsStats(Compilation compilation) {

        return CompilationMapper.toCompilationDto(compilationRepository.save(compilation),
                eventService.getEventsStatsByIds(compilation.getEvents()
                        .stream()
                        .map(Event::getId)
                        .collect(Collectors.toList())));
    }


    @Override
    public List<CompilationDto> getAll(boolean pinned, int from, int size) {

        PageRequest page = PageRequest.of(from / size, size, Sort.by("id").ascending());

        List<Compilation> compilations = compilationRepository.findByPinned(pinned, page);


        return compilations.stream()
                .map(c -> CompilationMapper.toCompilationDto(c,
                        eventService.getEventsStatsByIds(c.getEvents()
                                .stream()
                                .map(Event::getId)
                                .collect(Collectors.toList()))))
                .collect(Collectors.toList());
    }


    @Override
    public CompilationDto get(long compId) {

        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new DataNotFoundException("compilation not exist, id - " + compId));

        return getCompilationDtoWithEventsStats(compilation);
    }
}
