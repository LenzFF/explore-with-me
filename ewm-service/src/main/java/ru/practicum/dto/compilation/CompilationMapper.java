package ru.practicum.dto.compilation;

import ru.practicum.dto.event.EventMapper;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CompilationMapper {

    public static CompilationDto toCompilationDto(Compilation compilation, Map<Long, Long> eventsStatMap) {

        CompilationDto compilationDto = new CompilationDto();
        compilationDto.setId(compilation.getId());
        compilationDto.setPinned(compilation.isPinned());
        compilationDto.setTitle(compilation.getTitle());

        Set<Event> events = compilation.getEvents();
        if (events == null || events.size() == 0) {
            return compilationDto;
        }

        compilationDto.setEvents(compilation.getEvents().stream()
                .map(e -> EventMapper.toEventShortDto(e,
                        eventsStatMap.getOrDefault(e.getId(), 0L)))
                .collect(Collectors.toSet()));

        return compilationDto;
    }


    public static Compilation fromNewCompilationDto(NewCompilationDto newCompilationDto, Set<Event> events) {

        Compilation compilation = new Compilation();
        compilation.setTitle(newCompilationDto.getTitle());
        compilation.setEvents(events);
        compilation.setPinned(newCompilationDto.isPinned());

        return compilation;
    }
}
