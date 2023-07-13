package ru.practicum.stat;

import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;

import java.util.List;

public interface StatService {

    void writeStat(EndpointHitDto newHit);

    List<ViewStatsDto> getStats(String start, String end, List<String> uris, boolean unique);
}
